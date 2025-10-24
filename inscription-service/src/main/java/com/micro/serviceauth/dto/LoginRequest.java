package com.micro.serviceauth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête de connexion.
 * <p>
 * Le backend accepte soit un username, soit un email dans {@code usernameOrEmail}.
 */
public record LoginRequest(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) { }
