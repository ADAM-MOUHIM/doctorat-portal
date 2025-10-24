package com.micro.serviceauth.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Projection publique d'un refresh token (pour page "Mes sessions" par ex.).
 * <p>
 * N'expose jamais le token en clair, uniquement des métadonnées.
 */
public record RefreshTokenResponse(
        UUID id,
        UUID familyId,
        String deviceId,
        String userAgent,
        String ipAddress,
        String status,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt
) { }
