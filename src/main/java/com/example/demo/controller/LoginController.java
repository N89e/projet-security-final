package com.example.demo.controller;

import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Contrôleur REST pour gérer l'authentification des utilisateurs,
 * incluant les opérations de login et d'inscription.
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserAppRepository userAppRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Authentifie un utilisateur avec son nom d'utilisateur et mot de passe,
     * et retourne un cookie JWT si la connexion réussit.
     *
     * @param userApp objet UserApp contenant username et password en clair
     * @return ResponseEntity avec un cookie JWT et un message de confirmation
     * @throws Exception si le nom d'utilisateur ou le mot de passe est invalide
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isPresent()) {
            UserApp user = userAppOptional.get();
            if (passwordEncoder.matches(userApp.getPassword(), user.getPassword())) {
                ResponseCookie cookie = jwtService.createAuthenticationToken(user);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body("connected");
            }
        }
        throw new Exception("Invalid username or password");
    }

    /**
     * Enregistre un nouvel utilisateur avec un rôle USER par défaut,
     * si le nom d'utilisateur n'existe pas déjà en base.
     *
     * @param userApp objet UserApp contenant username et password en clair
     * @return ResponseEntity avec un message de confirmation d'inscription
     * @throws Exception si le nom d'utilisateur existe déjà
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isEmpty()) {
            String hashedPassword = passwordEncoder.encode(userApp.getPassword());
            UserApp newUser = new UserApp();
            newUser.setUsername(userApp.getUsername());
            newUser.setPassword(hashedPassword);
            newUser.setRole(Role.USER); // Rôle USER par défaut
            userAppRepository.save(newUser);
            return ResponseEntity.ok("User registered");
        } else {
            throw new Exception("Username already exists");
        }
    }
}
