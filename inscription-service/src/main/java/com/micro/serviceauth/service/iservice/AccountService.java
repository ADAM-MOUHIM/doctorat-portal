package com.micro.serviceauth.service.iservice;

import com.micro.serviceauth.dto.AccountResponse;
import com.micro.serviceauth.dto.RegisterRequest;
import com.micro.serviceauth.dto.RegisterResponse;

import java.util.UUID;

/**
 * Contrat du service de gestion des comptes (création, consultation).
 */
public interface AccountService {

    /**
     * Crée un compte à partir de la requête d'inscription.
     * <p>Hash du mot de passe, vérification d'unicité email/username, statut initial.</p>
     *
     * @param request données d'inscription
     * @return projection publique du compte nouvellement créé
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * Retourne la projection publique d'un compte par son identifiant.
     *
     * @param id identifiant du compte
     * @return projection publique
     */
    AccountResponse getById(UUID id);
}
