package com.micro.serviceauth.service.iservice;

import com.micro.serviceauth.dto.CreateDoctorantProfileRequest;
import com.micro.serviceauth.dto.CreateEncadrantProfileRequest;
import com.micro.serviceauth.dto.DoctorantProfileResponse;
import com.micro.serviceauth.dto.EncadrantProfileResponse;

import java.util.UUID;

/**
 * Contrat des opérations de profil (Doctorant / Encadrant) basées sur @MapsId.
 */
public interface ProfileService {

    /**
     * Crée le profil de type Doctorant pour le compte donné.
     * <p>Vérifie que le rôle primaire du compte est DOCTORANT.</p>
     */
    DoctorantProfileResponse createDoctorantProfile(CreateDoctorantProfileRequest request);

    /**
     * Crée le profil de type Encadrant pour le compte donné.
     * <p>Vérifie que le rôle primaire du compte est DIRECTEUR.</p>
     */
    EncadrantProfileResponse createEncadrantProfile(CreateEncadrantProfileRequest request);

    /**
     * Récupère le profil Doctorant par identifiant de compte (PK partagée).
     */
    DoctorantProfileResponse getDoctorantProfile(UUID accountId);

    /**
     * Récupère le profil Encadrant par identifiant de compte (PK partagée).
     */
    EncadrantProfileResponse getEncadrantProfile(UUID accountId);
}
