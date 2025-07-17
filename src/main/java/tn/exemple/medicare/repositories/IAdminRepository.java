package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;

import java.util.List;

public interface IAdminRepository   extends JpaRepository<User, Long> {

   @Query("SELECT d FROM Doctor d")
    List<Doctor> findAllDoctors();

    @Query("SELECT p FROM Patient p")
    List<Patient> findAllPatients();
}
