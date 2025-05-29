package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.dto.MedicalRecordDTO;
import tn.exemple.medicare.entities.dto.MedicalRecordResponse;

public interface IMedicalRecordServies {
    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto);
    MedicalRecordResponse getMedicalRecord() ;
    MedicalRecordResponse getMedicalRecordPatient(Long patientId);
    void deleteMedicalRecord();
    MedicalRecordDTO updateMedicalRecordPartial(MedicalRecordDTO dto);
}
