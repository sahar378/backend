package backend.configuration;
//Configure la sécurité de l'application (Spring Security)
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import backend.authentification.JwtAuthenticationFilter;
import backend.services.UserDetailsServiceImp;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	 private final UserDetailsServiceImp userDetailsServiceImp;

	    private final JwtAuthenticationFilter jwtAuthenticationFilter;
	    private final CustomLogoutHandler logoutHandler;

	    public SecurityConfiguration(UserDetailsServiceImp userDetailsServiceImp,
	                          JwtAuthenticationFilter jwtAuthenticationFilter , CustomLogoutHandler logoutHandler) {
	        this.userDetailsServiceImp = userDetailsServiceImp;
	        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	        this.logoutHandler = logoutHandler;
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        return http
	                .csrf(AbstractHttpConfigurer::disable)
	                .cors(cors -> cors.configurationSource(request -> {
	                    var corsConfiguration = new CorsConfiguration();
	                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // Origines autorisées
	                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Méthodes autorisées
	                    corsConfiguration.setAllowedHeaders(List.of("*")); // En-têtes autorisés
	                    corsConfiguration.setAllowCredentials(true); // Autoriser les cookies
	                    return corsConfiguration;
	                }))
	                .authorizeHttpRequests(req -> req
	                        .requestMatchers("/login/**", "/register/**").permitAll() // Autoriser l'accès public à /login et /register
	                       // .requestMatchers("/user/update").authenticated() // Exiger une authentification pour /user/update
	                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // Restreindre l'accès à /admin aux utilisateurs ayant le rôle ADMIN
	                        .requestMatchers("/admin/users/**").hasAuthority("ADMIN") // Restreindre l'accès à /admin/users aux utilisateurs ayant le rôle ADMIN
	                        .requestMatchers("/responsable_stock/**").hasAuthority("RESPONSABLE_STOCK") // Restreindre l'accès à /responsable_stock aux utilisateurs ayant le rôle RESPONSABLE_STOCK
	                        .requestMatchers("/personnel_medical/**").hasAuthority("PERSONNEL_MEDICAL") // Restreindre l'accès à /personnel_medical aux utilisateurs ayant le rôle PERSONNEL_MEDICAL
	                        .requestMatchers("/user").hasAuthority("USER") // Restreindre l'accès à /user aux utilisateurs ayant le rôle USER
	                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
	                )
	                .userDetailsService(userDetailsServiceImp)
	                .logout(logout -> logout
	                        .logoutUrl("/api/auth/logout") // Configurer la route de déconnexion
	                        .addLogoutHandler(logoutHandler)
	                        .logoutSuccessHandler((request, response, authentication) -> {
	                            SecurityContextHolder.clearContext();
	                            response.setStatus(HttpStatus.OK.value());
	                            response.getWriter().write("Logged out successfully");
	                        })
	                )
	                .sessionManagement(session -> session
	                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configurer la gestion des sessions comme stateless
	                )
	                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ajouter le filtre JWT
	                .build();
	    }
// Retourne un encodeur de mot de passe
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
	        return configuration.getAuthenticationManager();
	    }

}                        