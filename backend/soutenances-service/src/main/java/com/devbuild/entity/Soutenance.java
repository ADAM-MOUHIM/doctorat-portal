package com.devbuild.entity;

import com.devbuild.enums.SoutenanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "soutenances")
@Getter
@Setter                       // ⬅️ génère setPrerequisitesValid, setStatus, setLocation, setScheduledDateTime, etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Soutenance {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "doctorant_account_id", nullable = false, updatable = false)
    private UUID doctorantAccountId;

    @Column(name = "thesis_title", nullable = false, length = 300)
    private String thesisTitle;

    @Column(name = "thesis_summary", length = 2000)
    private String thesisSummary;

    @Column(name = "manuscript_url", length = 500)
    private String manuscriptUrl;

    @Column(name = "anti_plagiarism_report_url", length = 500)
    private String antiPlagiarismReportUrl;

    @Column(name = "publications_report_url", length = 500)
    private String publicationsReportUrl;

    @Column(name = "training_certificates_url", length = 500)
    private String trainingCertificatesUrl;

    @Column(name = "publications_count")
    private Integer publicationsCount;

    @Column(name = "conferences_count")
    private Integer conferencesCount;

    @Column(name = "training_hours")
    private Integer trainingHours;

    // ---------- CHAMPS QUI SERVENT AUX SETTERS DANS TON SERVICE ----------

    @Column(name = "prerequisites_valid", nullable = false)
    @Builder.Default
    private boolean prerequisitesValid = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private SoutenanceStatus status = SoutenanceStatus.DRAFT;

    @Column(name = "scheduled_datetime")
    private LocalDateTime scheduledDateTime;

    @Column(name = "location", length = 200)
    private String location;

    // ---------- Audit / version ----------

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Builder.Default
    @Column(name = "version", nullable = false)
    private long version = 0L;
}
