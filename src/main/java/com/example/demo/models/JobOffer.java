package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant une offre d'emploi.
 *
 * Chaque offre est identifiée par un identifiant unique généré automatiquement.
 * Elle contient un titre, une description, et référence l'utilisateur créateur.
 */
@Getter //J'ai eu des problèmes avec les getters et les setters
@Setter
@Entity
public class JobOffer {

    /**
     * Identifiant unique auto-généré de l'offre d'emploi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre de l'offre d'emploi.
     */
    private String title;

    /**
     * Description détaillée de l'offre d'emploi.
     */
    private String description;

    /**
     * Référence vers l'utilisateur qui a créé cette offre.
     * Relation many-to-one vers l'entité UserApp.
     */
    @ManyToOne
    private UserApp creator;
}
