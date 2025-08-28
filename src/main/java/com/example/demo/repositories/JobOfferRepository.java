package com.example.demo.repositories;

import com.example.demo.models.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour gérer les opérations CRUD sur les entités JobOffer.
 */
@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    /**
     * Récupère la liste complète des offres d'emploi.
     *
     * @return liste des offres d'emploi
     */
    List<JobOffer> findAll();

}
