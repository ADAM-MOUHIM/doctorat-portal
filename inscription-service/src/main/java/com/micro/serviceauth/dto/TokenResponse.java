package com.micro.serviceauth.dto;

/**
 * Paire de tokens retournée après un login ou un refresh.
 * <p>
 * {@code tokenType} = "Bearer".
 */
public record TokenResponse(
        String tokenType,
        String accessToken,
        long   accessExpiresIn,
        String refreshToken,
        long   refreshExpiresIn
) { }
