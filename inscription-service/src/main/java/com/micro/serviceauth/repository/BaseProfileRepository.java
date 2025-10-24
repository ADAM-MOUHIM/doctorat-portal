package com.micro.serviceauth.repository;

import com.micro.serviceauth.entity.BaseProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Accès générique aux profils (classe de base abstraite).
 * Utile si tu veux naviguer sans connaître le sous-type à l'avance.
 */
public interface BaseProfileRepository extends JpaRepository<BaseProfile, UUID> {

    /**
     * Récupère le profil (quel que soit le sous-type) par l'ID de compte (PK partagée).
     */
    Optional<BaseProfile> findByAccount_Id(UUID accountId);

    /**
     * Indique si un profil existe déjà pour ce compte (garantit 1 profil max / compte).
     */
    boolean existsByAccount_Id(UUID accountId);
}
