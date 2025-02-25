//Représente la réponse renvoyée après l'enregistrement : sans token ou l'authentification :avec token.
package backend.dto;

import backend.models.User;

public class AuthenticationResponse {

    private String token; // Peut être null dans le cas de l'enregistrement
    private String message;

    private String role;
    private User user; // Ajoutez un champ pour l'objet utilisateur
    

 // Constructeur pour l'authentification (avec token)
    public AuthenticationResponse(String token, String role, String message, User user) {
        this.token = token;
        this.role = role;
        this.message = message;
        this.user = user;
    }

    // Constructeur pour l'enregistrement (sans token)
    public AuthenticationResponse(String message) {
        this.token = null;
        this.message = message;
    }
    

    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}