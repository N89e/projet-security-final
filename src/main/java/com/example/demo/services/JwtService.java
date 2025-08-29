package com.example.demo.services;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Service de gestion du JSON Web Token (JWT) utilisé pour l'authentification.
 * Ce service fournit la génération, la validation du token
 * ainsi que le filtrage des requêtes HTTP pour extraire et valider le token JWT.
 */
@Service
public class JwtService extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.cookie_name}")
    private String COOKIE_NAME;

    /**
     * Durée de validité du token JWT en millisecondes.
     * Ici, 5 heures.
     */
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Autowired
    UserAppRepository userAppRepository;

    /**
     * Filtre exécuté pour chaque requête HTTP afin d'extraire, valider
     * et authentifier l'utilisateur via le JWT contenu dans un cookie.
     *
     * @param request  requête HTTP entrante
     * @param response réponse HTTP
     * @param filterChain chaîne de filtres
     * @throws ServletException en cas d'erreur servlet
     * @throws IOException en cas d'erreur I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getCookies() != null) {
            Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                    .map(Cookie::getValue)
                    .forEach(token -> {
                        try {
                            Claims claims = Jwts.parser()
                                    .setSigningKey(SECRET)
                                    .parseClaimsJws(token)
                                    .getBody();

                            Optional<UserApp> optUserApp = userAppRepository.findByUsername(claims.getSubject());
                            if (optUserApp.isEmpty()) {
                                throw new UsernameNotFoundException(claims.getSubject());
                            }
                            UserApp userApp = optUserApp.get();

                            if (validateToken(token, userApp)) {
                                String role = (String) claims.get("role");

                                List<SimpleGrantedAuthority> authorities =
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                                UsernamePasswordAuthenticationToken authenticationToken =
                                        new UsernamePasswordAuthenticationToken(userApp, null, authorities);

                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            }

                        } catch (Exception e) {
                            // En cas d'erreur, supprimer le cookie JWT
                            Cookie expiredCookie = new Cookie(COOKIE_NAME, null);
                            expiredCookie.setPath("/");
                            expiredCookie.setHttpOnly(true);
                            expiredCookie.setMaxAge(0);
                            response.addCookie(expiredCookie);
                        }
                    });
        } else {
            System.out.println("JwtService: No cookies found in request.");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Valide un token JWT par rapport à un utilisateur donné.
     *
     * @param token le token JWT
     * @param userApp l'utilisateur correspondant
     * @return true si le token est valide, false sinon
     */
    public Boolean validateToken(String token, UserApp userApp) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            boolean valid = (username.equals(userApp.getUsername()) && expiration.after(new Date()));

            System.out.println("JwtService: Token validation result: " + valid);
            return valid;
        } catch (Exception e) {
            System.out.println("JwtService: Token validation exception: " + e.getMessage());
            return false;
        }
    }

    /**
     * Génère un token JWT signé pour un utilisateur donné.
     *
     * @param userApp l'utilisateur
     * @return le token JWT sous forme de chaîne
     */
    public String generateToken(UserApp userApp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userApp.getUsername());
        claims.put("role", userApp.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userApp.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * Crée un cookie HTTP contenant le token JWT pour l'authentification.
     *
     * @param userApp l'utilisateur authentifié
     * @return le cookie HTTP avec le token JWT
     * @throws Exception en cas d'erreur lors de la création du token
     */
    public ResponseCookie createAuthenticationToken(UserApp userApp) throws Exception {
        try {
            final String token = generateToken(userApp);
            System.out.println("JwtService: Generated JWT token for user: " + userApp.getUsername());

            return ResponseCookie.from(COOKIE_NAME, token)
                    .httpOnly(true)
                    .path("/")
                    .build();
        } catch (DisabledException e) {
            System.out.println("JwtService: DisabledException during token creation");
            throw new Exception();
        }
    }
}
