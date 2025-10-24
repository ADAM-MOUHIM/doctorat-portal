package com.micro.serviceauth.dto;

import java.time.LocalDate;

/**
 * Bloc d'informations communes à tous les profils (embeddable côté entités).
 * <p>
 * Saisies de base du profil : identité et adresse.
 */
public record CommonProfileInfoDTO(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address
) { }
