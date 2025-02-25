package backend.authentification;
//Il garantit que seules les requêtes avec un token JWT valide sont autorisées.
// Filtre chaque requête pour vérifier la présence et la validité du token JWT. 
//Si le token est valide, il authentifie l'utilisateur et définit son contexte de sécurité.ce qui permet à Spring Security de gérer les autorisations.
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import backend.models.Token;
import backend.repositories.TokenRepository;
import backend.services.JwtService;
import backend.services.UserDetailsServiceImp;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;
	private final TokenRepository tokenRepository;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImp userDetailsService,TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
             @NonNull HttpServletResponse response,
             @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    	//Extraire le Token de l'En-tête
        String authHeader = request.getHeader("Authorization");
        //Cela permet aux endpoints publics (comme /login ou /register) de ne pas nécessiter de token.
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
     // Vérifiez si le token existe dans la base de données
        Optional<Token> tokenEntityOptional = tokenRepository.findByToken(token);

        if (tokenEntityOptional.isEmpty()) {
            // Token non trouvé, rejetez la requête
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token not found");
            return;
        }

        Token tokenEntity = tokenEntityOptional.get();

        // Vérifiez si le token est invalidé
        if (tokenEntity.isLoggedOut()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is invalid");
            return;
        }
        String username = jwtService.extractUsername(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtService.isValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}