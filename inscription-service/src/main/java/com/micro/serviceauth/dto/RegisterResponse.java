package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Réponse après création de compte.
 * <p>
 * Permet d'indiquer l'ID du compte et l'état initial (ex: PENDING).
 */
public record RegisterResponse(
        UUID accountId,
        String username,
        String email,
        String status,
        String primaryRole,
        boolean profileCompleted
) { }
