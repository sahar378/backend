package backend.authentification;
//Gère les endpoints pour l'authentification (enregistrement et connexion).
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.AuthenticationResponse;
import backend.models.User;
import backend.services.JwtService;

@RestController
public class AuthenticationController {
	@Autowired
    private JwtService jwtService;
    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User request
            ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
    	if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
        return ResponseEntity.ok(authService.authenticate(request));
    }
        
}