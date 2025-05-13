package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.mappers.PrescriptionMapper;
import tn.exemple.medicare.repositories.*;
import tn.exemple.medicare.services.IPrescriptionServices;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PrescriptionServices implements IPrescriptionServices {
    private final IUserRepository iUserRepository;
    private final IPrescriptionRepository iPrescriptionRepository;
    private final IDoseRepository iDoseRepository;
    private final MedicationServices medicationServices;
    private final IMedicationRepository iMedicationRepository;
    private  final PrescriptionMapper prescriptionMapper;
    private  final AuthService authService;
    private  final NotificationRepository notificationRepository;

    public Prescription createPrescription(PrescriptionDto prescriptionDto) {

        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with Id: '" + userId + "' not found"));
        Medication medication = iMedicationRepository.findByDenomination(prescriptionDto.getMedication().getDenomination());
        if (medication == null) {
            List<Medication> medications = medicationServices.searchMedicationsByName(
                    prescriptionDto.getMedication().getDenomination()
            ).block();
            if (medications == null || medications.isEmpty()) {
                medication = new Medication();
                medication.setDenomination(prescriptionDto.getMedication().getDenomination());
                medication = iMedicationRepository.save(medication);
            } else {
                medication = medications.get(0);
                medication = iMedicationRepository.save(medication);
            }
        }

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDto);
        prescription.setUser(user);
        prescription.setMedication(medication);

        // Lier les doses
        if (prescriptionDto.getDoses() != null && !prescriptionDto.getDoses().isEmpty()) {
            List<Dose> doses = prescriptionDto.getDoses().stream()
                    .map(doseRequest -> {
                        Dose dose = new Dose();
                        dose.setTimeToTake(doseRequest.getTimeToTake());
                        dose.setQuantity(doseRequest.getQuantity());
                        dose.setPrescription(prescription);
                        return dose;
                    })
                    .collect(Collectors.toList());
            prescription.setDoses(doses);
        }

        return iPrescriptionRepository.save(prescription);
    }

    @Override
    public List<Prescription> getPrescriptions() {

        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));

          List<Prescription> prescriptions = iPrescriptionRepository.findByUserId(userId);
        return prescriptions;
    }
    @Override
    public Prescription getPrescriptionById(Long prescriptionId ) {
        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));

        Prescription prescription = iPrescriptionRepository.findByUserIdAndId(userId, prescriptionId);
        if (prescription == null) {
            throw new EntityNotFoundException("Prescription with ID: " + prescriptionId + " for user ID: " + userId + " not found");
        }

        return prescription;
    }
    @Override
    @Transactional
    public void deletePrescription(Long prescriptionId) {
        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));
        Prescription prescription = iPrescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription with ID: " + prescriptionId + " not found"));

        if (prescription.getUser().getId() != userId) {
            throw new AccessDeniedException("You do not have permission to delete this prescription");
        }
        if (prescription.getDoses() != null && !prescription.getDoses().isEmpty()) {
            iDoseRepository.deleteAll(prescription.getDoses());
        }

        notificationRepository.nullifyPrescriptionReferences(prescriptionId);
        iPrescriptionRepository.delete(prescription);
    }

    @Override
    public Prescription updatePrescriptionPartial(Long prescriptionId, PrescriptionDto prescriptionDto) {
        Prescription existingPrescription = iPrescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription with Id: '" + prescriptionId + "' not found"));

        Long userId = authService.getAuthenticatedUserId();
        User user = iUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));

        if (prescriptionDto.getDurationDays() != 0) {
            existingPrescription.setDurationDays(prescriptionDto.getDurationDays());
        }

        if (prescriptionDto.getStockActuel() != null) {
            existingPrescription.setStockActuel(prescriptionDto.getStockActuel());
        }

        if (prescriptionDto.getMedication() != null && prescriptionDto.getMedication().getDenomination() != null) {
            String denomination = prescriptionDto.getMedication().getDenomination();
            Optional<Medication> medicationOpt = iMedicationRepository.findOneByDenomination(denomination);

            Medication medication = medicationOpt.orElseGet(() -> {
                Medication newMed = new Medication();
                newMed.setDenomination(denomination);
                return iMedicationRepository.save(newMed);
            });

            existingPrescription.setMedication(medication);
        }

        if (prescriptionDto.getDoses() != null) {
            List<Dose> existingDoses = existingPrescription.getDoses();
            List<PrescriptionDto.DoseDto> newDoses = prescriptionDto.getDoses();
            while (existingDoses.size() < newDoses.size()) {
                Dose newDose = new Dose();
                newDose.setPrescription(existingPrescription);
                //newDose.setNotified(false);
                existingDoses.add(newDose);
            }

            while (existingDoses.size() > newDoses.size()) {
                existingDoses.remove(existingDoses.size() - 1);
            }

            for (int i = 0; i < newDoses.size(); i++) {
                PrescriptionDto.DoseDto doseDto = newDoses.get(i);
                Dose existingDose = existingDoses.get(i);
                existingDose.setTimeToTake(doseDto.getTimeToTake());
                existingDose.setQuantity(doseDto.getQuantity());
                existingDose.setNotified(false);
            }
        }
        return iPrescriptionRepository.save(existingPrescription);
    }


}