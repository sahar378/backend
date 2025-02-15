package backend.services;
import backend.models.Role;
//contient la logique métier pour les opérations CRUD sur les utilisateurs.
import backend.models.User;
import backend.repositories.TokenRepository;
import backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;
    private final TokenRepository tokenRepository; // Ajoutez ce repository

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Récupérer un utilisateur par son ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Créer un nouvel utilisateur
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Mettre à jour un utilisateur existant
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setFirstname(userDetails.getFirstname());
        user.setLastname(userDetails.getLastname());
        user.setPassword(userDetails.getPassword());
        return userRepository.save(user);
    }

 // Supprimer un utilisateur
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
    public List<User> getPendingUsers() {
        return userRepository.findByRole(Role.USER);
    }
    //pour mettre à jour le rôle d'un utilisateur
    public User updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userRepository.save(user);
    }
    //filtrer les utilisateurs en se basant sur leur role , n3adi role eli mahachtich bih 
    public List<User> getUsersByRoleNot(Role role) {
        return userRepository.findByRoleNot(role);
    }
}