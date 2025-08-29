package com.example.demo;

import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité Spring Security pour l'application.
 * <p>
 * Définit la gestion des sessions en mode stateless avec JWT,
 * les règles d'accès aux endpoints selon les rôles,
 * et intègre le filtre JWT personnalisé.
 * </p>
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Bean PasswordEncoder pour encoder les mots de passe utilisateurs
     * avec l'algorithme BCrypt.
     *
     * @return un encodeur de mots de passe BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définit la chaîne de filtres de sécurité HTTP pour configurer
     * les règles d'accès, la gestion de session stateless, et la désactivation CSRF.
     *
     * @param http l'objet HttpSecurity pour configurer la sécurité web
     * @return la SecurityFilterChain configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactive la protection CSRF, utile pour les API REST JWT stateless
                .csrf(csrf -> csrf.disable())

                // Définit la gestion de session en mode Stateless (pas de session HTTP)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuration des règles d'accès aux URLs de l'application
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics accessibles sans authentification
                        .requestMatchers("/auth/login", "/auth/register", "/hello/public", "/h2-console/**").permitAll()

                        // Accès public en lecture aux offres d'emploi
                        .requestMatchers(HttpMethod.GET, "/jobs/**").permitAll()

                        // Accès réservé aux utilisateurs avec rôle ADMIN
                        .requestMatchers("/hello/private-admin").hasRole("ADMIN")

                        // Toutes autres requêtes sur /jobs nécessitent authentification
                        .requestMatchers("/jobs/**").authenticated()

                        // Toutes les autres requêtes nécessitent authentification
                        .anyRequest().authenticated()
                )

                // Ajoute le filtre JWT avant le filtre d'authentification standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtService, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
