package com.devbuild.dto;

import com.devbuild.enums.JuryRole;

import java.util.UUID;

public record JuryMemberResponse(
        UUID id,
        String fullName,
        String email,
        String institution,
        JuryRole role,
        boolean external
) {}