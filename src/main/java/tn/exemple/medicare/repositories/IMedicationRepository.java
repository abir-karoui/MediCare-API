package tn.exemple.medicare.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Medication;

import java.awt.print.Pageable;
import java.util.List;

@Repository

public interface IMedicationRepository extends JpaRepository<Medication, Long > {
    List<Medication> findAllByDenomination(String denomination);
    List<Medication> deleteMedicationsByDenomination(String denomination);
}
