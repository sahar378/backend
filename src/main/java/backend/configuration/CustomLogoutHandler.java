package backend.configuration;
// Invalide le token JWT lors de la déconnexion en marquant le token comme loggedOut dans la base de données.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import backend.models.Token;
import backend.repositories.TokenRepository;

import java.util.Date;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("En-tête Authorization manquant ou invalide.");
            return;
        }

        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        if (storedToken == null) {
            System.out.println("Token non trouvé dans la base de données.");
            return;
        }
            storedToken.setLoggedOut(true); // Marquer le token comme invalide
            storedToken.setLogoutTimestamp(new Date()); // Enregistrer la date de déconnexion
            tokenRepository.save(storedToken);
            System.out.println("Token marqué comme invalide : " + storedToken.getToken());
            System.out.println("Date de déconnexion : " + storedToken.getLogoutTimestamp());
        
    }
}