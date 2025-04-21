package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.prescription.Dose;

@Repository
public interface IDoseRepository extends JpaRepository<Dose, Long> {
}
