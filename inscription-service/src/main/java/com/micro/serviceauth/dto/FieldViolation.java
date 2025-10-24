package com.micro.serviceauth.dto;

/**
 * Détail d'une erreur de validation sur un champ.
 */
public record FieldViolation(
        String field,
        String message,
        Object rejectedValue
) { }
