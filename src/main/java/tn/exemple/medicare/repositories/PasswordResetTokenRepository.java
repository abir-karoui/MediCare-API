package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.exemple.medicare.entities.PasswordResetToken;
import tn.exemple.medicare.entities.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByResetToken(String resetToken);
    void deleteByUser(User user);

}
