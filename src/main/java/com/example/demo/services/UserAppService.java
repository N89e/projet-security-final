package com.example.demo.services;

import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service utilisateur implémentant UserDetailsService pour Spring Security.
 * Permet de charger les informations utilisateur pour l'authentification
 * et d'enregistrer un nouvel utilisateur avec encodeur de mot de passe.
 */
@Service
public class UserAppService implements UserDetailsService {

    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur injectant le repository utilisateur et le password encoder.
     *
     * @param userRepository repository pour accéder aux utilisateurs
     * @param passwordEncoder encodeur de mots de passe
     */
    public UserAppService(UserAppRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Enregistre un nouvel utilisateur avec le role et mot de passe encodé.
     *
     * @param username nom d'utilisateur
     * @param rawPassword mot de passe en clair
     * @param role rôle de l'utilisateur (USER ou ADMIN)
     * @return l'utilisateur sauvé en base
     */
    public UserApp registerUser(String username, String rawPassword, Role role) {
        UserApp user = new UserApp();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Charge un utilisateur par son nom pour Spring Security.
     * Convertit le rôle en SimpleGrantedAuthority avec préfixe ROLE_.
     *
     * @param username nom d'utilisateur cherché
     * @return UserDetails utilisé par Spring Security pour authentification
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
