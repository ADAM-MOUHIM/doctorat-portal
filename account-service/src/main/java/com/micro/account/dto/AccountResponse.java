package com.micro.account.dto;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        String email,
        String phone,
        String status,
        String primaryRole,
        boolean profileCompleted,
        boolean emailVerified
) { }

