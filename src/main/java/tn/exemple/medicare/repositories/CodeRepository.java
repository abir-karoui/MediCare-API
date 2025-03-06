package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.Codes;

import java.util.Optional;
@Repository
public interface CodeRepository extends JpaRepository<Codes, Long> {
    Optional<Codes> findByCode(String code);
}
