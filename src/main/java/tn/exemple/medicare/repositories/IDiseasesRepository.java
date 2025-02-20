package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.Diseases;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;

import java.util.List;

@Repository
public interface IDiseasesRepository extends JpaRepository<Diseases, Long> {
    Diseases findDiseasesByName(String name);

}
