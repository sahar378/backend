//lena masina
package backend.authentification;
//Implémente la logique métier pour l'enregistrement et l'authentification.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import backend.dto.AuthenticationResponse;
import backend.models.Token;
import backend.models.User;
import backend.repositories.TokenRepository;
import backend.repositories.UserRepository;
import backend.services.JwtService;

import java.util.List;

@Service
public class AuthenticationService {
	private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
	private final TokenRepository tokenRepository;

    public AuthenticationService(UserRepository repository,PasswordEncoder passwordEncoder,JwtService jwtService,
    		AuthenticationManager authenticationManager , TokenRepository tokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }
    public AuthenticationResponse register(User request) {
        // Vérifie si l'utilisateur existe déjà
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse("User already exists");
        }

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);

        return new AuthenticationResponse("User registered successfully");
    }

    public AuthenticationResponse authenticate(User request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = repository.findByUsername(request.getUsername()).orElseThrow();
            String token = jwtService.generateToken(user);
         // Enregistre le token dans la base de données
            Token tokenEntity = new Token();
            tokenEntity.setToken(token);
            tokenEntity.setLoggedOut(false);
            tokenEntity.setUser(user);
            tokenRepository.save(tokenEntity);
           /* return new AuthenticationResponse(token, "User authenticated successfully");
        } catch (AuthenticationException e) {
            return new AuthenticationResponse(null, "Authentication failed: Invalid username or password");
        }*/
            
            
         // Renvoie le token, le rôle, le message et l'objet utilisateur complet
            return new AuthenticationResponse(token, user.getRole().name(), "User authenticated successfully", user);
        } catch (AuthenticationException e) {
            return new AuthenticationResponse(null, null, "Authentication failed: Invalid username or password", null);
        }
    
    }

}
