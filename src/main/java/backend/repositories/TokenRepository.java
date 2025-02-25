package backend.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.models.Token;
import backend.models.User;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);// Trouve un token par sa valeur.
    void deleteByUser(User user); // Méthode pour Supprime tous les tokens associés à un utilisateur.;
    List<Token> findByUser(User user); // Retourne une liste de tokens
 // Supprimer les tokens invalides antérieurs à une date donnée
    void deleteByLoggedOutTrueAndLogoutTimestampBefore(Date cutoffDate);
    //récupérer les tokens dont loggedOut = true = 1 , invalides
	List<Token> findByLoggedOutTrue();

}