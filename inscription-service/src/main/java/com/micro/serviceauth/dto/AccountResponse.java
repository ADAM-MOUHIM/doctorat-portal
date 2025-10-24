package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Projection "publique" d'un compte.
 * <p>
 * N'expose aucun secret (hash), uniquement des métadonnées utiles au front.
 */
public record AccountResponse(
        UUID id,
        String username,
        String email,
        String phone,
        String status,
        String primaryRole,
        boolean profileCompleted
) { }
