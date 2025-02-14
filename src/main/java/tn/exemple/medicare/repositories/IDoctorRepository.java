package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Doctor;
@Repository
public interface IDoctorRepository extends JpaRepository<Doctor, Long> {

}
