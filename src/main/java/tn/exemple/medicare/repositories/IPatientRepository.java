package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Patient;
@Repository
public interface IPatientRepository extends JpaRepository<Patient, Long> {


}
