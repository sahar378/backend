package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import backend.services.JwtService;
import backend.services.TokenCleanupService;

@RestController
public class DemoController {
//demo : Accessible à tous les utilisateurs authentifiés.
	@Autowired
	 private  TokenCleanupService tokenCleanupService;
	@Autowired
    private JwtService jwtService;

    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello from secured url");
    }
    // Endpoint pour l'utilisateur standard
 // Endpoint pour l'admin
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> adminSpace() {
        return ResponseEntity.ok("Hello ADMIN");
    }

    // Endpoint pour l'utilisateur standard
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> userSpace() {
        return ResponseEntity.ok("Hello USER");
    }

    // Endpoint pour le responsable stock
    @GetMapping("/responsable_stock")
    @PreAuthorize("hasAuthority('RESPONSABLE_STOCK')")
    public ResponseEntity<String> responsableStockSpace() {
        return ResponseEntity.ok("Hello RESPONSABLE_STOCK");
    }

    // Endpoint pour le personnel médical
    @GetMapping("/personnel_medical")
    @PreAuthorize("hasAuthority('PERSONNEL_MEDICAL')")
    public ResponseEntity<String> personnelMedicalSpace() {
        return ResponseEntity.ok("Hello PERSONNEL_MEDICAL");
    }
    
    @GetMapping("/trigger-cleanup")
    public String triggerCleanup() {
        tokenCleanupService.cleanupExpiredTokens();
        return "Tâche de nettoyage déclenchée manuellement !";
    }
}