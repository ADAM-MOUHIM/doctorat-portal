package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Requête de création du profil encadrant (directeur).
 * <p>
 * Les IDs de département/labo sont des UUID référentiels (découplage).
 */
public record CreateEncadrantProfileRequest(
        UUID accountId,
        CommonProfileInfoDTO info,
        String grade,
        UUID departmentId,
        UUID laboratoryId
) { }
