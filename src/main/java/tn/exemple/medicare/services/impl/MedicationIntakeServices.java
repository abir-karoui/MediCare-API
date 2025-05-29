package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.prescription.MedicationIntake;
import tn.exemple.medicare.repositories.IMedicationIntakeRepository;
import tn.exemple.medicare.repositories.IPatientRepository;
import tn.exemple.medicare.services.IMedicationIntakeServices;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MedicationIntakeServices implements IMedicationIntakeServices {
    private  final AuthService authService;
    private final IMedicationIntakeRepository medicationIntakeRepository;
    private  final IPatientRepository iPatientRepository;


    @Override
    public MedicationIntake markIntakeAsTaken(Long intakeId, boolean taken) {
        Long userId = authService.getAuthenticatedUserId();
        Patient patient = iPatientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with ID: '" + userId + "' not found"));

        MedicationIntake intake = medicationIntakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntityNotFoundException("MedicationIntake with ID " + intakeId + " not found"));

        Long intakePatientId = intake.getPrescription().getPatient().getId();

        if (!intakePatientId.equals(patient.getId())) {
            throw new SecurityException("You are not authorized to modify this medication intake.");
        }
        intake.setTaken(taken);
        return medicationIntakeRepository.save(intake);
    }
}
