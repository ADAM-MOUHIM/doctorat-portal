package com.micro.account.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID accountId,
        String username,
        String email,
        String status,
        String primaryRole,
        boolean profileCompleted,
        boolean emailVerified
) { }

