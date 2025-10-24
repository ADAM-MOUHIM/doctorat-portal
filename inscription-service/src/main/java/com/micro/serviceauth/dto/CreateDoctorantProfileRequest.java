package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Requête de création du profil doctorant.
 * <p>
 * Avec Plan B (@MapsId), {@code accountId} = future PK du profil.
 */
public record CreateDoctorantProfileRequest(
        UUID accountId,
        CommonProfileInfoDTO info,
        String diploma,
        Integer graduationYear,
        String university
) { }
