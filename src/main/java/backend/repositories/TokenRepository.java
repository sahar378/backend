package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.models.Token;
import backend.models.User;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByUser(User user); // Méthode pour supprimer les tokens par utilisateur
}