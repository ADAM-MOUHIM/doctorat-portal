package com.devbuild.dto;

import com.devbuild.enums.JuryRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JuryMemberRequest(
        @NotBlank String fullName,
        String email,
        String institution,
        @NotNull JuryRole role,
        boolean external
) {}