package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.medicalRecord.MedicalRecord;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMedicalRecordRepository  extends JpaRepository<MedicalRecord, Long> {
    boolean existsByPatient(Patient patient);
   MedicalRecord findByPatientId(Long userId);
    Optional<MedicalRecord> findByPatient(Patient patient);
}
