package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Diseases;
@Repository
public interface IDiseasesRepository extends JpaRepository<Diseases, Long> {
}
