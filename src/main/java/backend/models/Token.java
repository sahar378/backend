package backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private boolean loggedOut;
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoutTimestamp; // Nouveau champ pour enregistrer la date de déconnexion

    public Token() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Token(Long id, String token, boolean loggedOut, User user) {
		super();
		this.id = id;
		this.token = token;
		this.loggedOut = loggedOut;
		this.user = user;
	}

	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	 public void setLogoutTimestamp(Date logoutTimestamp) {
	        this.logoutTimestamp = logoutTimestamp;
	    }
	    public Date getLogoutTimestamp() {
	        return logoutTimestamp;
	    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
//retourne la valeur du token JWT.
	public String getToken() {
		return token;
	}
//Définit la valeur du token JWT.
	public void setToken(String token) {
		this.token = token;
	}
// Vérifie si le token est invalidé (déconnexion).
	public boolean isLoggedOut() {
		return loggedOut;
	}
//Définit l'état du token (valide ou invalidé)
	public void setLoggedOut(boolean loggedOut) {
		this.loggedOut = loggedOut;
	}
//Retourne l'utilisateur associé au token.
	public User getUser() {
		return user;
	}
//Associe un utilisateur au token.
	public void setUser(User user) {
		this.user = user;
	}

    
}