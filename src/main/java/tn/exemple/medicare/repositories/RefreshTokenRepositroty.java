package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.exemple.medicare.entities.auth.RefreshToken;
import tn.exemple.medicare.entities.auth.User;

import java.util.Optional;
@Repository
public interface RefreshTokenRepositroty extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);
}
