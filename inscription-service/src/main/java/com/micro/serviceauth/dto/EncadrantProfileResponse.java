package com.micro.serviceauth.dto;

import java.util.UUID;

/**
 * Projection "publique" d'un profil encadrant.
 */
public record EncadrantProfileResponse(
        UUID id,
        UUID accountId,
        CommonProfileInfoDTO info,
        String grade,
        UUID departmentId,
        UUID laboratoryId
) { }
