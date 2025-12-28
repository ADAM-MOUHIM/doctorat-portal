package com.devbuild.dto;

public record FieldViolation(
        String field,
        Object rejectedValue,
        String message
) {}