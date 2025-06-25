package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Medication;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPrescriptionRepository  extends JpaRepository<Prescription, Long> {
    //List<Prescription> findPrescriptionByUser(Long userId);
  /*  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Prescription p " +
            "WHERE p.patient = :patient AND p.medication.denomination = :denomination")
    boolean existsByPatientAndMedicationDenomination(@Param("patient") Patient patient, @Param("denomination") String denomination);
*/
    List<Prescription> findByPatientAndMedicationDenomination(Patient patient, String denomination);


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
    int countByDoctorId(Long doctorId);
    @Query(value = """
    SELECT DATE(created_at) as day, COUNT(*) as count
    FROM prescription
    WHERE doctor_id = :doctorId
      AND created_at >= CURRENT_DATE - INTERVAL '6 day'
    GROUP BY DATE(created_at)
    ORDER BY day
    """, nativeQuery = true)
    List<Object[]> countByDoctorIdGroupedByDayLast7Days(@Param("doctorId") Long doctorId);





}
