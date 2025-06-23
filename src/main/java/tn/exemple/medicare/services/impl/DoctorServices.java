package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.mappers.PrescriptionMapper;
import tn.exemple.medicare.repositories.*;
import tn.exemple.medicare.services.IDoctor;
import tn.exemple.medicare.services.INotificationServices;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DoctorServices implements IDoctor {

    private final  IDoctorRepository iDoctorRepository;
    private final IPatientRepository iPatientRepository;
    private final IPrescriptionRepository iPrescriptionRepository;
    private final IDoseRepository iDoseRepository;
    private final MedicationServices medicationServices;
    private final IMedicationRepository iMedicationRepository;
    private  final PrescriptionMapper prescriptionMapper;
    private  final AuthService authService;
    private  final NotificationRepository notificationRepository;
    private  final InvitationServices invitationServices;
    private final INotificationServices iNotificationServices;
    private  final  IMedicationIntakeRepository iMedicationIntakeRepository;

    @Override
    public Prescription createPrescriptionForPatient(Long patientId, PrescriptionDto prescriptionDto) {

        Long doctorId = authService.getAuthenticatedUserId();
        Doctor doctor = iDoctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: '" + doctorId + "' not found"));
        if (!doctor.getRole().equals(TypeRole.DOCTOR)) {
            throw new IllegalStateException("Only doctors can create prescriptions.");
        }
        Patient patient = iPatientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with ID: '" + patientId + "' not found"));


        if (!invitationServices.areUsersConnected(patientId)) {
            throw new IllegalStateException("You are not connected to this patient. Cannot create prescription.");
        }

        Medication medication = iMedicationRepository.findByDenomination(prescriptionDto.getMedication().getDenomination());
        if (medication == null) {
            List<Medication> medications = medicationServices.searchMedicationsByName(
                    prescriptionDto.getMedication().getDenomination()
            ).block();

            if (medications == null || medications.isEmpty()) {
                medication = new Medication();
                medication.setDenomination(prescriptionDto.getMedication().getDenomination());
            } else {
                medication = medications.get(0);
            }

            medication = iMedicationRepository.save(medication);
        }

        List<Prescription> existingPrescriptions = iPrescriptionRepository
                .findByPatientAndMedicationDenomination(patient, prescriptionDto.getMedication().getDenomination());

        boolean activeExists = existingPrescriptions.stream()
                .anyMatch(Prescription::isActive);

        if (activeExists) {
            throw new BusinessException(BusinessErrorCode.PRESCRIPTION_ALREADY_EXISTS);
        }

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDto);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedication(medication);

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

        Prescription savedPrescription = iPrescriptionRepository.save(prescription);

        iNotificationServices.sendPrescriptionNotificationToPatient(patient, doctor, savedPrescription);

        LocalDate today = LocalDate.now();
        for (Dose dose : savedPrescription.getDoses()) {
            MedicationIntake intake = MedicationIntake.builder()
                    .date(today)
                    .timeToTake(dose.getTimeToTake())
                    .quantity(dose.getQuantity())
                    .medicationName(medication.getDenomination())
                    .prescription(savedPrescription)
                    .patient(patient)
                    .taken(false)
                    .notified(false)
                    .build();

            iMedicationIntakeRepository.save(intake);
        }
        return savedPrescription;


    }

    @Override
    public List<Prescription> getPrescriptions(Long patientId) {
    Long doctorId = authService.getAuthenticatedUserId();
    Doctor doctor = iDoctorRepository.findById(doctorId)
            .orElseThrow(() -> new EntityNotFoundException("Doctor with ID: " + doctorId + " not found"));

    //List<Prescription> prescriptions = iPrescriptionRepository.findByDoctorId(doctorId);
       List<Prescription> prescriptions = iPrescriptionRepository.findByDoctorIdAndPatientIdOrderByCreatedAtDesc(doctorId, patientId);

       return prescriptions;
    }


   @Override
   public List<Prescription> getPrescriptionsNotCreatedByDoctor(Long patientId) {
       Long doctorId = authService.getAuthenticatedUserId();

       Doctor doctor = iDoctorRepository.findById(doctorId)
               .orElseThrow(() -> new EntityNotFoundException("Doctor with ID: " + doctorId + " not found"));

       return iPrescriptionRepository.findPrescriptionsByPatientExcludingCurrentDoctor(patientId, doctorId);
   }

    @Override
   @Transactional
   public void deletePrescription(Long prescriptionId) {
       long doctorId = authService.getAuthenticatedUserId();

       Doctor doctor = iDoctorRepository.findById(doctorId)
               .orElseThrow(() -> new EntityNotFoundException("Doctor with ID: " + doctorId + " not found"));

       Prescription prescription = iPrescriptionRepository.findById(prescriptionId)
               .orElseThrow(() -> new EntityNotFoundException("Prescription with ID: " + prescriptionId + " not found"));

       if (prescription.getDoctor() == null
               || prescription.getDoctor().getId() != doctorId) {
           throw new AccessDeniedException("You can only delete prescriptions you have created.");
       }

       if (prescription.getDoses() != null && !prescription.getDoses().isEmpty()) {
           iDoseRepository.deleteAll(prescription.getDoses());
       }

       notificationRepository.nullifyPrescriptionReferences(prescriptionId);
       iPrescriptionRepository.delete(prescription);
   }

    @Override
    public List<Prescription> getAllPrescriptionsForPatient(Long patientId) {
        Long doctorId = authService.getAuthenticatedUserId();

        iDoctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor with ID: " + doctorId + " not found"));

        List<Prescription> allPrescriptions = iPrescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        return allPrescriptions.stream()
                .filter(Prescription::isActive)
                .collect(Collectors.toList());
    }





}
