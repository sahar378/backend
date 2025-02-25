package backend.controllers;
//Ce contrôleur gère les endpoints réservés à l'administrateur pour la gestion des utilisateurs (CRUD).
//Seuls les utilisateurs ayant le rôle ADMIN peuvent accéder à ces endpoints.
import backend.dto.RoleUpdateRequest;
import backend.models.Role;
//controlleur de l'admin
import backend.models.User;
import backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

	private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // Créer un nouvel utilisateur
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    // Mettre à jour un utilisateur existant
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String message = userService.deleteUser(id);
        return ResponseEntity.ok(message);
    }
    
    //zedna lena 
    //pour récupérer les utilisateurs en attente (ayant le rôle USER)
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    }
    // Mettre à jour le rôle d'un utilisateur.
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        Role role = Role.valueOf(request.getNewRole()); // Convertir la chaîne en enum Role
        User updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }
    //Récupérer la liste des utilisateurs n'ayant pas un rôle spécifique.
    @GetMapping("/not-role/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRoleNot(@PathVariable Role role) {
        List<User> users = userService.getUsersByRoleNot(role);
        return ResponseEntity.ok(users);
    }
}
