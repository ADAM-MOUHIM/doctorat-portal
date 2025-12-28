package com.micro.auth.dto;

public record FieldViolation(
        String field,
        String message,
        Object rejectedValue
) { }

