package tn.exemple.medicare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.exemple.medicare.entities.RefreshToken;
import tn.exemple.medicare.entities.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    //List<Token> findAllValidTokenByUser( Long userId);

}
