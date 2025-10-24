package com.micro.serviceauth.dto;

import java.time.Instant;
import java.util.List;

/**
 * Payload d'erreur renvoyé au client (cohérent pour toutes les erreurs).
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
) { }
