package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.Role;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les opérations CRUD sur les offres d'emploi.
 *
 * Toutes les méthodes nécessitent une authentification préalable.
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/jobs")
public class JobOfferController {

    private final JobOfferRepository jobOfferRepository;
    private final UserAppRepository userAppRepository;

    /**
     * Constructeur injectant les repositories nécessaires.
     *
     * @param jobOfferRepository repository pour gérer les offres d'emploi
     * @param userAppRepository repository pour gérer les utilisateurs
     */
    public JobOfferController(JobOfferRepository jobOfferRepository, UserAppRepository userAppRepository) {
        this.jobOfferRepository = jobOfferRepository;
        this.userAppRepository = userAppRepository;
    }

    /**
     * Liste toutes les offres d'emploi.
     * Accessible uniquement aux utilisateurs authentifiés.
     *
     * @return liste des offres d'emploi
     */
    @GetMapping
    public List<JobOffer> listAllJobs() {
        return jobOfferRepository.findAll();
    }

    /**
     * Ajoute une nouvelle offre d'emploi liée à l'utilisateur connecté.
     *
     * @param jobOffer l'offre d'emploi à ajouter
     * @param authentication objet d'authentification représentant l'utilisateur connecté
     * @return réponse HTTP avec confirmation ou erreur
     */
    @PostMapping
    public ResponseEntity<?> addJob(@RequestBody JobOffer jobOffer, Authentication authentication) {
        try {
            if (authentication == null) {
                System.out.println("addJob: Authentication object is null");
                return ResponseEntity.status(401).body("Authentication is required");
            }
            Object principal = authentication.getPrincipal();
            UserApp user;

            if (principal instanceof UserApp) {
                user = (UserApp) principal;
                System.out.println("addJob: User principal from authentication: " + user.getUsername());
            } else {
                String username = authentication.getName();
                System.out.println("addJob: Username from authentication: " + username);
                user = userAppRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            }

            jobOffer.setCreator(user);
            jobOfferRepository.save(jobOffer);
            System.out.println("addJob: Job offer saved successfully");

            return ResponseEntity.ok("Job offer created");

        } catch (Exception e) {
            System.out.println("addJob: Exception caught - " + e.getMessage());
            return ResponseEntity.status(500).body("Error creating job offer: " + e.getMessage());
        }
    }

    /**
     * Supprime une offre d'emploi par son id.
     * Seuls l'utilisateur créateur ou un administrateur peuvent supprimer l'offre.
     *
     * @param id l'id de l'offre d'emploi à supprimer
     * @param authentication objet d'authentification représentant l'utilisateur connecté
     * @return réponse HTTP avec confirmation, erreur ou refus d'autorisation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication) {
        JobOffer jobOffer = jobOfferRepository.findById(id).orElse(null);
        if (jobOffer == null) {
            System.out.println("deleteJob: JobOffer not found with id " + id);
            return ResponseEntity.notFound().build();
        }

        Object principal = authentication.getPrincipal();
        UserApp user;

        if (principal instanceof UserApp) {
            user = (UserApp) principal;
            System.out.println("deleteJob: User principal from authentication: " + user.getUsername());
        } else {
            String username = authentication.getName();
            System.out.println("deleteJob: Username from authentication: " + username);
            try {
                user = userAppRepository.findByUsername(username).orElseThrow();
            } catch (Exception e) {
                System.out.println("deleteJob: User not found: " + username);
                return ResponseEntity.status(401).body("User not found");
            }
        }

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isCreator = jobOffer.getCreator() != null &&
                jobOffer.getCreator().getUsername().equals(user.getUsername());

        if (isAdmin || isCreator) {
            jobOfferRepository.delete(jobOffer);
            System.out.println("deleteJob: Job offer deleted by user " + user.getUsername());
            return ResponseEntity.ok("Job offer deleted");
        } else {
            System.out.println("deleteJob: User not authorized to delete job offer");
            return ResponseEntity.status(403).body("Not authorized");
        }
    }

}
