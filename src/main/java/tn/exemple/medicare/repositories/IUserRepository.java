package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    List<User> findAllByRole(TypeRole role);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);


}
