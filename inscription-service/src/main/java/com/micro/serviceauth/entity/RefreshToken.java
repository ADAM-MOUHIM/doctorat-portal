package com.micro.serviceauth.entity;

import com.micro.serviceauth.enums.RefreshTokenStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Refresh token persistant pour gérer rotation, révocation, logout et multi-device.
 * <p>
 * Sécurité : on NE stocke PAS le token en clair, mais son {@code tokenHash} (SHA-256 hex).
 * {@code tokenValue} est seulement renvoyé au client, jamais sauvegardé en clair en base.
 */
@Entity
@Table(name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = "token_hash")
        },
        indexes = {
                @Index(name = "idx_rft_account", columnList = "account_id"),
                @Index(name = "idx_rft_family",  columnList = "family_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    /** Identifiant technique du refresh token (PK). */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    /** Compte propriétaire (plusieurs tokens possibles par compte / par device). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rft_account"))
    private Account account;

    /** Hash SHA-256 du refresh token (hex), unique. */
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    /** Famille de tokens (pour rotation continue / "token family"). */
    @Column(name = "family_id", columnDefinition = "uuid", nullable = false)
    private UUID familyId;

    /** Date d'expiration du refresh token. */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Statut (ACTIVE/REVOKED/REPLACED/EXPIRED). */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private RefreshTokenStatus status = RefreshTokenStatus.ACTIVE;

    /** ID du token qui remplace celui-ci lors d'une rotation (optionnel). */
    @Column(name = "replaced_by_id", columnDefinition = "uuid")
    private UUID replacedById;

    /** Infos device (facultatives mais utiles pour UX/sécurité). */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    /** Métadonnées de suivi. */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** Verrou optimiste pour éviter les courses lors de rotations concurrentes. */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private long version = 0L;

    /** Indique si le token est expiré par rapport à {@code expiresAt}. */
    @Transient
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
