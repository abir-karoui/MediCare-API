package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Medication;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;

import java.util.List;

@Repository

public interface IMedicationRepository extends JpaRepository<Medication, Long > {
    List<Medication> findAllByDenomination(String denomination);
    List<Medication> deleteMedicationsByDenomination(String denomination);
}
