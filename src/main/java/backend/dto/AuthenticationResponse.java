//lena masina zedna role
package backend.dto;
//Représente la réponse renvoyée après l'enregistrement ou l'authentification.
public class AuthenticationResponse {

    private String token; // Peut être null dans le cas de l'enregistrement
    private String message;

    private String role;

    
    // Constructeur pour l'authentification (avec token)
    public AuthenticationResponse(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// Constructeur pour l'enregistrement (sans token)
    public AuthenticationResponse(String message) {
        this.token = null;
        this.message = message;
    }

    // Getters et setters
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