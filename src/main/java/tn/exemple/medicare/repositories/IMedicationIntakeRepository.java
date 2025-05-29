package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.prescription.MedicationIntake;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IMedicationIntakeRepository extends JpaRepository<MedicationIntake, Long> {
    /*@Query("SELECT m FROM MedicationIntake m " +
            "WHERE m.date = CURRENT_DATE " +
            "AND m.timeToTake BETWEEN :now AND :oneMinuteLater " +
            "AND m.notified = false")
    List<MedicationIntake> findTodayIntakesToNotify(@Param("now") LocalTime now, @Param("oneMinuteLater") LocalTime oneMinuteLater);
*/
    List<MedicationIntake> findByDateAndTimeToTakeAndNotifiedFalse(LocalDate date, LocalTime timeToTake);

}
