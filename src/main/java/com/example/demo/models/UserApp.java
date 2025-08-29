package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un utilisateur de l'application.
 * Chaque utilisateur possède un identifiant unique, un nom d'utilisateur unique,
 * un mot de passe encodé et un rôle (USER ou ADMIN).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_app")
public class UserApp {

    /**
     * Identifiant unique auto-généré de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nom d'utilisateur unique.
     */
    @Column(unique = true)
    private String username;

    /**
     * Mot de passe encodé de l'utilisateur.
     */
    private String password;

    /**
     * Rôle de l'utilisateur dans l'application.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Constructeur avec initialisation des champs username, password et role.
     *
     * @param username le nom d'utilisateur
     * @param password le mot de passe encodé
     * @param role     le rôle attribué
     */
    public UserApp(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
