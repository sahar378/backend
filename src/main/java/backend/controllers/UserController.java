package backend.controllers;
//Ce contrôleur gère les endpoints liés à l'utilisateur connecté, comme la mise à jour de ses informations et la récupération de ses détails.
import backend.dto.AuthenticationResponse;
import backend.models.Token;
import backend.models.User;
import backend.repositories.TokenRepository;
import backend.services.JwtService;
import backend.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService; // Injecter JwtService
    
    @Autowired
    private TokenRepository tokenRepository;

    // Mettre à jour les informations de l'utilisateur connecté
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, Authentication authentication) {
        // Récupérer le nom d'utilisateur connecté
        String username = authentication.getName();
        System.out.println("Utilisateur connecté: " + username); // Log pour débogage

        // Mettre à jour les informations de l'utilisateur dans la base de données
        User user = userService.updateUserInfo(username, updatedUser);

        // Invalider tous les anciens tokens de l'utilisateur
        List<Token> oldTokens = tokenRepository.findByUser(user);
        if (!oldTokens.isEmpty()) {
            for (Token oldToken : oldTokens) {
                oldToken.setLoggedOut(true); // Marquer chaque ancien token comme invalide
                tokenRepository.save(oldToken); // Sauvegarder les modifications
                System.out.println("Ancien token invalidé : " + oldToken.getToken()); // Log pour débogage
            }
        }

        // Générer un nouveau token avec les informations mises à jour
        String token = jwtService.generateToken(user);
        System.out.println("Nouveau token généré: " + token); // Log pour débogage

        // Enregistrer le nouveau token dans la base de données
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setLoggedOut(false); // Le nouveau token est valide
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);

        // Retourner le nouveau token et les informations de l'utilisateur
        return ResponseEntity.ok(new AuthenticationResponse(
                token, 
                user.getRole().name(), 
                "Informations mises à jour avec succès", 
                user
        ));
    }

    // Récupérer les informations de l'utilisateur connecté
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}