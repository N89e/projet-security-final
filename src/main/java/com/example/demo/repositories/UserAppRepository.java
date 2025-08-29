package com.example.demo.repositories;

import com.example.demo.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour gérer les opérations sur les entités UserApp.
 */
@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur recherché
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<UserApp> findByUsername(String username);
}
