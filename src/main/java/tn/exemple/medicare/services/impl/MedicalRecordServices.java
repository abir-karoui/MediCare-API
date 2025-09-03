package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.MedicalRecordDTO;
import tn.exemple.medicare.entities.dto.MedicalRecordResponse;
import tn.exemple.medicare.entities.dto.OperationDTO;
import tn.exemple.medicare.entities.dto.PrescriptionDto;
import tn.exemple.medicare.entities.medicalRecord.MedicalRecord;
import tn.exemple.medicare.entities.medicalRecord.Operation;
import tn.exemple.medicare.entities.prescription.Dose;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.mappers.MedicalRecordMapper;
import tn.exemple.medicare.repositories.IDoctorRepository;
import tn.exemple.medicare.repositories.IMedicalRecordRepository;
import tn.exemple.medicare.repositories.IOperationRepository;
import tn.exemple.medicare.repositories.IPatientRepository;
import tn.exemple.medicare.services.IMedicalRecordServies;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordServices implements IMedicalRecordServies {

    private final AuthService authService;
    private final IMedicalRecordRepository iMedicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final IPatientRepository patientRepository;
    private  final InvitationServices invitationServices;
    private final IDoctorRepository iDoctorRepository;
    @Override

    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto) {
        Long userId = authService.getAuthenticatedUserId();

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with Id: '" + userId + "' not found"));

        if (iMedicalRecordRepository.existsByPatient(patient)) {
            throw new IllegalStateException("Medical record already exists for this patient.");
        }

        MedicalRecord medicalRecord = medicalRecordMapper.toEntity(dto, patient);


        MedicalRecord savedRecord = iMedicalRecordRepository.save(medicalRecord);

        return medicalRecordMapper.toDto(savedRecord);
    }

    @Override
   public MedicalRecordResponse getMedicalRecord() {
       Long userId = authService.getAuthenticatedUserId();

       Patient patient = patientRepository.findById(userId)
               .orElseThrow(() -> new EntityNotFoundException("Patient with Id: '" + userId + "' not found"));

       MedicalRecord record = iMedicalRecordRepository.findByPatientId(userId);

       if (record == null) {
               throw new BusinessException(BusinessErrorCode.MEDICAL_RECORD_NOT_FOUND);
       }
        return medicalRecordMapper.toResponse(record, patient);
   }


    @Override
    public MedicalRecordResponse getMedicalRecordPatient(Long patientId) {
        Long doctorId = authService.getAuthenticatedUserId();

        Doctor doctor = iDoctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor with ID: '" + doctorId + "' not found"));

        if (!invitationServices.areUsersConnected(patientId)) {
            throw new IllegalStateException("You are not connected to this patient. Cannot access medical record.");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with ID: '" + patientId + "' not found"));

        MedicalRecord record = iMedicalRecordRepository.findByPatientId(patientId);
        if (record == null) {
            throw new BusinessException(BusinessErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }

        return medicalRecordMapper.toResponse(record, patient);
    }

    @Override
    public void deleteMedicalRecord() {
        Long userId = authService.getAuthenticatedUserId();

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with ID: '" + userId + "' not found"));

        MedicalRecord record = iMedicalRecordRepository.findByPatientId(userId);

        if (record == null) {
            throw new BusinessException(BusinessErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }

        iMedicalRecordRepository.delete(record);
    }
    @Override
    @Transactional
    public MedicalRecordDTO updateMedicalRecordPartial(MedicalRecordDTO dto) {
        Long userId = authService.getAuthenticatedUserId();

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with Id: '" + userId + "' not found"));

        MedicalRecord medicalRecord = iMedicalRecordRepository.findByPatient(patient)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord not found for patient with ID: " + userId));

        // Update des champs simples
        if (dto.getBloodType() != null) medicalRecord.setBloodType(dto.getBloodType());
        if (dto.getHeight() != null) medicalRecord.setHeight(dto.getHeight());
        if (dto.getWeight() != null) medicalRecord.setWeight(dto.getWeight());
        if (dto.getAllergies() != null) medicalRecord.setAllergies(dto.getAllergies());
        if (dto.getChronicDiseases() != null) medicalRecord.setChronicDiseases(dto.getChronicDiseases());

        // Traitement des opérations si fournies
        if (dto.getOperations() != null) {
            Map<Long, Operation> existingOpsById = medicalRecord.getOperations().stream()
                    .collect(Collectors.toMap(Operation::getId, op -> op));

            Set<Long> incomingIds = dto.getOperations().stream()
                    .map(OperationDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Supprimer les opérations absentes dans la nouvelle liste
            List<Operation> toRemove = medicalRecord.getOperations().stream()
                    .filter(op -> op.getId() != null && !incomingIds.contains(op.getId()))
                    .collect(Collectors.toList());

            medicalRecord.getOperations().removeAll(toRemove);

            // Ajouter ou mettre à jour les opérations
            for (OperationDTO opDto : dto.getOperations()) {
                if (opDto.getId() != null && existingOpsById.containsKey(opDto.getId())) {
                    // Update
                    Operation existing = existingOpsById.get(opDto.getId());
                    existing.setName(opDto.getName());
                    existing.setDateOperation(opDto.getDateOperation());
                    existing.setSurgeon(opDto.getSurgeon());
                    existing.setHospital(opDto.getHospital());
                    existing.setDescription(opDto.getDescription());
                } else {
                    // Add new
                    Operation newOp = new Operation();
                    newOp.setName(opDto.getName());
                    newOp.setDateOperation(opDto.getDateOperation());
                    newOp.setSurgeon(opDto.getSurgeon());
                    newOp.setHospital(opDto.getHospital());
                    newOp.setDescription(opDto.getDescription());
                    newOp.setMedicalRecord(medicalRecord);
                    medicalRecord.getOperations().add(newOp);
                }
            }
        }

        MedicalRecord saved = iMedicalRecordRepository.save(medicalRecord);
        return medicalRecordMapper.toDto(saved);
    }








}
