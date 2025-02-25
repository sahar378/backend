package backend.services;
//Gère la génération, la validation et l'extraction des informations des tokens JWT.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import backend.models.Token;
import backend.models.User;
import backend.repositories.TokenRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function; 

@Service
public class JwtService {
	@Autowired
    private TokenRepository tokenRepository;
	 @Value("${application.security.jwt.secret-key}")
		private String SECRET_KEY;
	 @Value("${application.security.jwt.expiration}")
	private  long EXPIRATION_TIME; 
//Génère un token JWT pour un utilisateur
	public String generateToken(User user) {
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + + 1000 * 60 * 60 * 10))
                .signWith(getSigninKey())
                .compact();

        return token;
    }
//Retourne la clé secrète utilisée pour signer le token
	    private SecretKey getSigninKey() {
	        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	    //Extrait toutes les informations (claims) du token
	    private Claims extractAllClaims(String token) {
	        return Jwts
	                .parser()
	                .verifyWith(getSigninKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }
	    //Extrait une information spécifique du token.
	    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
	        Claims claims = extractAllClaims(token);
	        return resolver.apply(claims);
	    }
	    // Extrait le nom d'utilisateur du token.
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }
	    /*public boolean isValid(String token, UserDetails user) {
	        String username = extractUsername(token);
	        return (username.equals(user.getUsername())) && !isTokenExpired(token);
	    }*/
	    // Vérifie si le token est valide et non expiré.
	    public boolean isValid(String token, UserDetails user) {
	        String username = extractUsername(token);
	        boolean isTokenValid = (username.equals(user.getUsername())) && !isTokenExpired(token);

	        // Vérifier si le token est marqué comme loggedOut dans la base de données
	        Token tokenEntity = tokenRepository.findByToken(token).orElse(null);
	        if (tokenEntity != null && tokenEntity.isLoggedOut()) {
	            isTokenValid = false;
	        }

	        return isTokenValid;
	    }
	    // Vérifie si le token est expiré.
	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }
	    // Extrait la date d'expiration du token.
	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }	   
}
