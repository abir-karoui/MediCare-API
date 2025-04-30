package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.prescription.Prescription;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface IPrescriptionRepository  extends JpaRepository<Prescription, Long> {
    //List<Prescription> findPrescriptionByUser(Long userId);
    List<Prescription> findByUserId(Long userId);
    Prescription findByUserIdAndId(Long userId , Long prescriptionId );



}
