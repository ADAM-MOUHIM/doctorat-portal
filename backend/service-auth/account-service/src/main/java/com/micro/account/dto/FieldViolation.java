package com.micro.account.dto;

public record FieldViolation(
        String field,
        String message,
        Object rejectedValue
) { }

