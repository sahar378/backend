package backend.models;
//Implémente : UserDetails pour intégrer l'utilisateur avec Spring Security.
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Le prénom est obligatoire")//Assure que le champ n'est pas vide.
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstname;
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastname;
    @NotBlank(message = "Le username est obligatoire")
    @Size(max = 20, message = "Le username ne doit pas dépasser 20 caractères")
    private String username;
    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 50, message = "L'email ne doit pas dépasser 50 caractères")
    @Email//Valide que la valeur du champ respecte le format d'une adresse e-mail standard.
    private String email;
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(max = 120, message = "Le mot de passe ne doit pas dépasser 50 caractères")
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    

    public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
		this.username = username;
	}
    @Override
    @JsonIgnore // Exclure cette propriété de la sérialisation/désérialisation JSON
    //elle retourne une liste contenant le rôle de l'utilisateur.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
//Vérifie si le compte de l'utilisateur n'est pas expiré
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
//Vérifie si le compte de l'utilisateur n'est pas verrouillé
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
//    Vérifie si les informations d'identification de l'utilisateur ne sont pas expirées
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
// Vérifie si le compte de l'utilisateur est activé
    @Override
    public boolean isEnabled() {
        return true;
    }
}