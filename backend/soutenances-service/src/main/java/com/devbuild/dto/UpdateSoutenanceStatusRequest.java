package com.devbuild.dto;

import com.devbuild.enums.SoutenanceStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSoutenanceStatusRequest(
        @NotNull SoutenanceStatus newStatus,
        String comment // optionnel : motif de rejet, remarque…
) {}