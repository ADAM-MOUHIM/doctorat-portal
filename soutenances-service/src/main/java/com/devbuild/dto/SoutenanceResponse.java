package com.devbuild.dto;

import com.devbuild.enums.SoutenanceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SoutenanceResponse(
        UUID id,
        UUID doctorantAccountId,
        String thesisTitle,
        String thesisSummary,
        String manuscriptUrl,
        String antiPlagiarismReportUrl,
        String publicationsReportUrl,
        String trainingCertificatesUrl,
        Integer publicationsCount,
        Integer conferencesCount,
        Integer trainingHours,
        boolean prerequisitesValid,
        SoutenanceStatus status,
        LocalDateTime scheduledDateTime,
        String location,
        List<JuryMemberResponse> jury
) {}