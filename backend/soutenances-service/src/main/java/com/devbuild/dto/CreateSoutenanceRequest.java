package com.devbuild.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateSoutenanceRequest(
        @NotNull UUID doctorantAccountId,
        @NotBlank String thesisTitle,
        String thesisSummary,
        String manuscriptUrl,
        String antiPlagiarismReportUrl,
        String publicationsReportUrl,
        String trainingCertificatesUrl,
        Integer publicationsCount,
        Integer conferencesCount,
        Integer trainingHours,
        LocalDateTime desiredDateTime,
        String desiredLocation,
        List<JuryMemberRequest> jury // optionnel : proposition de jury directement
) {}