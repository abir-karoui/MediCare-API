package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Dose;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface IDoseRepository extends JpaRepository<Dose, Long> {

    @Query("SELECT d FROM Dose d WHERE " +
            "d.timeToTake BETWEEN :start AND :end AND " +
            "d.notified = false AND " +
            "d.prescription.user.fcmToken IS NOT NULL")
    List<Dose> findUnnotifiedDosesNearNow(
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );
    @Modifying
    @Query("UPDATE Dose d SET d.notified = false")
    void resetAllNotifiedDoses();
}
