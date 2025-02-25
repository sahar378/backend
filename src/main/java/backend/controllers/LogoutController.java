package backend.controllers;
//Ce contrôleur gère la déconnexion des utilisateurs en invalidant leur token JWT.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.configuration.CustomLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final CustomLogoutHandler logoutHandler;

    @Autowired
    public LogoutController(CustomLogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        // Appeler le CustomLogoutHandler pour invalider le token
        logoutHandler.logout(request, response, authentication);

        // Retourner une réponse de succès
        return ResponseEntity.ok("Logged out successfully");
    }
}