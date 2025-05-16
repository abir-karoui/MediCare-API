package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPrescriptionRepository  extends JpaRepository<Prescription, Long> {
    //List<Prescription> findPrescriptionByUser(Long userId);
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long userId);
    List<Prescription> findByDoctorId(Long userId);
    Optional<Prescription> findByIdAndDoctorId(Long prescriptionId, Long doctorId);
    Optional<Prescription> findByIdAndPatientId(Long prescriptionId, Long patientId);
   /* @Query("SELECT p FROM Prescription p WHERE p.doctor IS NULL OR p.doctor.id <> :doctorId")
    List<Prescription> findPrescriptionsNotCreatedByDoctorIncludingNull(@Param("doctorId") Long doctorId);

*/
   @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND (p.doctor IS NULL OR p.doctor.id <> :doctorId)")
   List<Prescription> findPrescriptionsByPatientExcludingCurrentDoctor(@Param("patientId") Long patientId, @Param("doctorId") Long doctorId);


    Prescription findByPatientIdAndId(Long userId , Long prescriptionId );
    List<Prescription> findByDoctorIdAndPatientIdOrderByCreatedAtDesc(Long doctorId, Long patientId);



}
