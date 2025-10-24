package com.micro.serviceauth.dto;

import com.micro.serviceauth.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Requête d'inscription d'un compte.
 * <p>
 * Utilisée par le service d'inscription pour créer un {@code Account}.
 * Le hash du mot de passe est géré côté service (pas ici).
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 60) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        String phone,
        Role primaryRole
) { }
