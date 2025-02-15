package backend.repositories;
import java.util.List;
//CRUD pour les utilisateurs
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import backend.models.Role;
import backend.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

	List<User> findByRole(Role user);

	List<User> findByRoleNot(Role role);// Récupère les utilisateurs qui n'ont pas le rôle spécifié
    
}