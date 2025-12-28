package com.micro.account.dto;

import java.util.UUID;

public record AccountAuthInfoResponse(
        UUID id,
        String username,
        String email,
        String passwordHash,
        String status,
        String primaryRole
) { }

