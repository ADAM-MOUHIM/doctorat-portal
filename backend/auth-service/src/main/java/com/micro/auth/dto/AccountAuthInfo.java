package com.micro.auth.dto;

import java.util.UUID;

public record AccountAuthInfo(
        UUID id,
        String username,
        String email,
        String passwordHash,
        String status,
        String primaryRole
) { }

