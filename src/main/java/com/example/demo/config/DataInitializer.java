package com.example.demo.config;

import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Composant Spring qui initialise les données de l'application au démarrage.
 *
 * Cette classe insère un utilisateur administrateur par défaut dans la base de données
 * avec le rôle ADMIN et un mot de passe encodé.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur injectant les dépendances nécessaires.
     *
     * @param userRepository le repository JPA pour accéder aux utilisateurs
     * @param passwordEncoder le service d’encodage de mots de passe
     */
    public DataInitializer(UserAppRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Méthode exécutée au démarrage de l’application.
     * Insère un utilisateur administrateur par défaut si besoin.
     *
     * @param args arguments de la ligne de commande
     * @throws Exception en cas d’erreur lors de l’insertion en base
     */
    @Override
    public void run(String... args) throws Exception {
        userRepository.save(new UserApp("admin", passwordEncoder.encode("admin"), Role.ADMIN));
        System.out.println("Données initiales insérées dans user_app");
    }

}
