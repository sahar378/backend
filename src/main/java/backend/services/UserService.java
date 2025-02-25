package backend.services;
import backend.dto.AuthenticationResponse;
import backend.models.Role;
//contient la logique métier pour les opérations CRUD sur les utilisateurs.
import backend.models.User;
import backend.repositories.TokenRepository;
import backend.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
@Autowired
private  PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
    private final TokenRepository tokenRepository; // Ajoutez ce repository
    
    private final EmailService emailService; // Ajoutez EmailService

    public UserService(UserRepository userRepository, TokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService; // Initialisez EmailService
    }

    // Récupérer tous les utilisateurs, fait par l'admin
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Récupérer un utilisateur par son ID , fait par l'admin
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Créer un nouvel utilisateur par l'admin et envoie d'email en cas d'attribution de role RESPONSABLE_STOCK ou PERSONNEL_MEDICAL
    public ResponseEntity<?> createUser(User user) {
        // Sauvegarder l'utilisateur dans la base de données
        //User savedUser = userRepository.save(user);
    	// Vérifie si l'utilisateur existe déjà
    	// Vérifie si l'utilisateur existe déjà
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("User already exists"));
        }
        // Hacher le mot de passe avant de le stocker
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Sauvegarder l'utilisateur dans la base de données
        User savedUser = userRepository.save(user);
        // Vérifier si le rôle est RESPONSABLE_STOCK ou PERSONNEL_MEDICAL
        if (user.getRole() == Role.RESPONSABLE_STOCK || user.getRole() == Role.PERSONNEL_MEDICAL) {
            try {
                String subject = "Bienvenue dans l'application";
                String text = "Bonjour " + user.getUsername() + ",\n\n"
                        + "Vous avez été inscrit avec succès dans l'application par l'administrateur.\n"
                        + "Votre rôle est : " + user.getRole() + ".\n\n";

                // Ajouter des informations spécifiques au rôle
                switch (user.getRole()) {
                    case RESPONSABLE_STOCK:
                        text += "En tant que responsable de stock, vous avez accès à la gestion des stocks.\n";
                        break;
                    case PERSONNEL_MEDICAL:
                        text += "En tant que personnel médical, vous avez accès aux dossiers des patients.\n";
                        break;
                }

                text += "\nCordialement,\nL'équipe de support";
                emailService.sendEmail(user.getEmail(), subject, text);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            }
        }

        return ResponseEntity.ok(new AuthenticationResponse("User created successfully"));
    }
    // Mettre à jour un utilisateur existant , fait par l'admin et envoie d'email dans le cas de changement de role 
    public User updateUser(Long id, User userDetails) {
    	// Récupérer l'utilisateur existant
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si le rôle a changé
        boolean roleChanged = !user.getRole().equals(userDetails.getRole());

        // Mettre à jour les informations de l'utilisateur
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstname(userDetails.getFirstname());
        user.setLastname(userDetails.getLastname());
       // user.setPassword(userDetails.getPassword());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));


        // Si le rôle a changé, appeler updateUserRole pour envoyer un e-mail
        if (roleChanged) {
            user.setRole(userDetails.getRole()); // Mettre à jour le rôle
            System.out.println("Utilisateur " + user.getUsername() + " mis à jour. Rôle changé : " + roleChanged);
            return updateUserRole(id, userDetails.getRole()); // Envoyer un e-mail
        }

        // Si le rôle n'a pas changé, sauvegarder simplement les modifications
        return userRepository.save(user);
    }

 // Supprimer un utilisateur et ses tokens associés , fait par l'admin
    @Transactional//démarrer une transaction
    public String deleteUser(Long id) {
        try {
            // 1. Récupère l'utilisateur
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Supprime les tokens associés
            tokenRepository.deleteByUser(user);

            // 3. Supprime l'utilisateur
            userRepository.delete(user);

            // Retourne un message de succès
            return "Utilisateur " + user.getUsername() + " supprimé avec succès.";

        } catch (Exception e) {
            // Retourne un message d'erreur
            return "Erreur lors de la suppression de l'utilisateur : " + e.getMessage();
        }
    }
    
    //lena badelna 
    //Retourne la liste des utilisateurs avec le rôle USER., fait par l'admin
    public List<User> getPendingUsers() {
        return userRepository.findByRole(Role.USER);
    }
    //Met à jour le rôle d'un utilisateur et envoie un e-mail de notification., fait par l'admin
    public User updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
       // return userRepository.save(user);
        userRepository.save(user);
        try {
            String subject = "Mise à jour de votre rôle";
            String text = "Bonjour " + user.getUsername() + ",\n\nVotre rôle a été mis à jour. Vous êtes maintenant " + newRole + ".\n\n";

            // Ajouter des informations spécifiques au rôle
            switch (newRole) {
                case RESPONSABLE_STOCK:
                    text += "En tant que responsable de stock, vous avez accès à la gestion des stocks.\n";
                    break;
                case PERSONNEL_MEDICAL:
                    text += "En tant que personnel médical, vous avez accès aux dossiers des patients.\n";
                    break;
                case ADMIN:
                    text += "En tant qu'administrateur, vous avez accès à toutes les fonctionnalités du système.\n";
                    break;
                default:
                    text += "Vous avez maintenant accès aux fonctionnalités de base.\n";
            }

            text += "\nCordialement,\nL'équipe de support";
            emailService.sendEmail(user.getEmail(), subject, text);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }

        return user;
    }

    
    //Retourne la liste des utilisateurs n'ayant pas un rôle spécifique. , n3adi role eli mahachtich bih , fait par l'admin
    public List<User> getUsersByRoleNot(Role role) {
        return userRepository.findByRoleNot(role);
    }
    
    
    
    //hathom ya3melhom user
   // Met à jour les informations d'un utilisateur par lui-même.    
    public User updateUserInfo(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour les champs modifiables
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return userRepository.save(user);
    }

    // Récupérer l'utilisateur par son nom d'utilisateur : fait par l'user 
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}