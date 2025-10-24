package com.micro.serviceauth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête de renouvellement de token d'accès via un refresh token.
 */
public record RefreshTokenRequest(
        @NotBlank String refreshToken
) { }
