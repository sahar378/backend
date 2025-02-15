package backend.services;
//Gère la génération, la validation et l'extraction des informations des tokens JWT.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import backend.models.Token;
import backend.models.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function; 

@Service
public class JwtService {
	 @Value("${application.security.jwt.secret-key}")
		private String SECRET_KEY;
	 @Value("${application.security.jwt.expiration}")
	private  long EXPIRATION_TIME; 

	public String generateToken(User user) {
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigninKey())
                .compact();

        return token;
    }

	    private SecretKey getSigninKey() {
	        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	    private Claims extractAllClaims(String token) {
	        return Jwts
	                .parser()
	                .verifyWith(getSigninKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }
	    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
	        Claims claims = extractAllClaims(token);
	        return resolver.apply(claims);
	    }
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }
	    public boolean isValid(String token, UserDetails user) {
	        String username = extractUsername(token);
	        return (username.equals(user.getUsername())) && !isTokenExpired(token);
	    }

	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }	   
}
