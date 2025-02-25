package backend.services;
import backend.models.Token;
//supprime les tokens invalides après un délai spécifié (par exemple, 30 jours) 
//Les tokens avec logged_out = true et logout_timestamp = NULL seront maintenant supprimés.
import backend.repositories.TokenRepository;
import jakarta.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TokenCleanupService {

    private final TokenRepository tokenRepository;

    public TokenCleanupService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Supprimer les tokens invalides après 30 jours
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Exécution tous les jours à minuit
    public void cleanupExpiredTokens() {
        System.out.println("Exécution de la tâche de nettoyage des tokens invalides...");
        Date currentDate = new Date(); // Date actuelle

        // Récupérer tous les tokens invalides -> loggedOut = true = 1
        List<Token> invalidTokens = tokenRepository.findByLoggedOutTrue();

        // Filtrer les tokens dont la déconnexion a eu lieu il y a plus de 30 jours ou sans date de déconnexion
        List<Token> tokensToDelete = invalidTokens.stream()
                .filter(token -> {
                    Date logoutTimestamp = token.getLogoutTimestamp();
                    if (logoutTimestamp == null) {
                        return true; // Supprimer les tokens sans date de déconnexion (invalidés mais sans timestamp)
                    }
                    long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000; // 30 jours en millisecondes
                    return logoutTimestamp.getTime() + thirtyDaysInMillis < currentDate.getTime();
                })
                .toList();

        System.out.println("Nombre de tokens à supprimer : " + tokensToDelete.size());

        // Supprimer les tokens correspondants
        tokenRepository.deleteAll(tokensToDelete);
        System.out.println("Tâche de nettoyage terminée.");
    }
}
/*Tokens Valides :

Les tokens valides (ceux où loggedOut = false) ne sont pas récupérés par findByLoggedOutTrue(), donc ils ne seront jamais supprimés.

Tokens Invalidés :

Les tokens invalidés (loggedOut = true) sont supprimés uniquement si :

Leur logoutTimestamp est antérieur à 30 jours. = baed 30 youm mel déconnexion

Ou leur logoutTimestamp est NULL (cas où la déconnexion n'a pas été correctement enregistrée 
par exemple lors de la modification de donnés : on génére un nuveau token et invalide l old token  .
*/