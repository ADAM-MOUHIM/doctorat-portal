package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Projection "publique" d'un profil doctorant.
 * <p>
 * {@code id} == {@code accountId} (clé primaire partagée via @MapsId).
 */
public record DoctorantProfileResponse(
        UUID id,
        UUID accountId,
        CommonProfileInfoDTO info,
        String diploma,
        Integer graduationYear,
        String university
) { }
