package com.micro.serviceauth.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.serviceauth.dto.DeviceInfo;
import com.micro.serviceauth.dto.TokenResponse;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.entity.RefreshToken;
import com.micro.serviceauth.enums.RefreshTokenStatus;
import com.micro.serviceauth.repository.RefreshTokenRepository;
import com.micro.serviceauth.service.iservice.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implémentation Pro+ :
 * - Génère le JWT d'accès HS256 (stateless) sans service externe.
 * - Persiste les refresh tokens (hash + famille) pour rotation/révocation/logout.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /** Issuer des JWT d'accès (application.yaml: app.jwt.issuer) */
    @Value("${app.jwt.issuer:serviceAuth}")
    private String jwtIssuer;

    /** Secret HMAC pour signer les JWT HS256 (application.yaml: app.jwt.secret) */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** TTL de l'access token en secondes (application.yaml: app.jwt.access-token-expiration) */
    @Value("${app.jwt.access-token-expiration:3600}")
    private long accessTtlSeconds;

    /** TTL du refresh token en secondes (application.yaml: app.jwt.refresh-token-expiration) */
    @Value("${app.jwt.refresh-token-expiration:2592000}")
    private long refreshTtlSeconds;

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64URL = Base64.getUrlEncoder().withoutPadding();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** {@inheritDoc} */
    @Override
    @Transactional
    public TokenResponse issueOnLogin(Account account, DeviceInfo device) {
        // 1) Access JWT
        Instant now = Instant.now();
        String access = generateAccessJwt(account, now);

        // 2) Refresh persistant (hash + famille)
        String refreshPlain = secureRandomToken();
        String refreshHash  = sha256Hex(refreshPlain);
        Instant expiresAt   = now.plus(refreshTtlSeconds, ChronoUnit.SECONDS);

        RefreshToken entity = RefreshToken.builder()
                .account(account)
                .tokenHash(refreshHash)
                .familyId(UUID.randomUUID())
                .expiresAt(expiresAt)
                .deviceId(device != null ? device.deviceId() : null)
                .userAgent(device != null ? device.userAgent() : null)
                .ipAddress(device != null ? device.ipAddress() : null)
                .createdAt(now)
                .status(RefreshTokenStatus.ACTIVE)
                .build();

        refreshTokenRepository.save(entity);

        return new TokenResponse("Bearer", access, accessTtlSeconds, refreshPlain, refreshTtlSeconds);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public TokenResponse rotate(String refreshTokenValue) {
        String hash = sha256Hex(refreshTokenValue);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inconnu."));

        if (current.getStatus() != RefreshTokenStatus.ACTIVE) {
            throw new IllegalStateException("Refresh token inactif (revoked/replaced/expired).");
        }
        if (current.isExpired()) {
            current.setStatus(RefreshTokenStatus.EXPIRED);
            refreshTokenRepository.save(current);
            throw new IllegalStateException("Refresh token expiré.");
        }

        Account account = current.getAccount();
        Instant now = Instant.now();

        // 1) Nouveau access JWT
        String access = generateAccessJwt(account, now);

        // 2) Nouveau refresh (même famille)
        String newPlain = secureRandomToken();
        String newHash  = sha256Hex(newPlain);
        Instant expiresAt = now.plus(refreshTtlSeconds, ChronoUnit.SECONDS);

        RefreshToken next = RefreshToken.builder()
                .account(account)
                .tokenHash(newHash)
                .familyId(current.getFamilyId())
                .expiresAt(expiresAt)
                .deviceId(current.getDeviceId())
                .userAgent(current.getUserAgent())
                .ipAddress(current.getIpAddress())
                .createdAt(now)
                .status(RefreshTokenStatus.ACTIVE)
                .build();
        refreshTokenRepository.save(next);

        // 3) Clore l'ancien
        current.setStatus(RefreshTokenStatus.REPLACED);
        current.setReplacedById(next.getId());
        current.setLastUsedAt(now);
        refreshTokenRepository.save(current);

        return new TokenResponse("Bearer", access, accessTtlSeconds, newPlain, refreshTtlSeconds);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void revoke(String refreshTokenValue) {
        String hash = sha256Hex(refreshTokenValue);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inconnu."));
        current.setStatus(RefreshTokenStatus.REVOKED);
        current.setRevokedAt(Instant.now());
        refreshTokenRepository.save(current);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public int revokeAllForAccount(UUID accountId) {
        var actives = refreshTokenRepository
                .findAllByAccount_IdAndStatus(accountId, RefreshTokenStatus.ACTIVE);
        Instant now = Instant.now();
        actives.forEach(t -> {
            t.setStatus(RefreshTokenStatus.REVOKED);
            t.setRevokedAt(now);
        });
        refreshTokenRepository.saveAll(actives);
        return actives.size();
    }

    /* =========================
       ===  JWT (HS256)  =======
       ========================= */

    /**
     * Génère un JWT HS256 signé, RFC7519, avec claims minimales :
     * sub (accountId), role, iss, iat, exp.
     */
    private String generateAccessJwt(Account account, Instant now) {
        try {
            long iat = now.getEpochSecond();
            long exp = now.plusSeconds(accessTtlSeconds).getEpochSecond();

            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", account.getId().toString());
            payload.put("role", account.getPrimaryRole().name());
            payload.put("iss", jwtIssuer);
            payload.put("iat", iat);
            payload.put("exp", exp);

            String h = B64URL.encodeToString(MAPPER.writeValueAsBytes(header));
            String p = B64URL.encodeToString(MAPPER.writeValueAsBytes(payload));
            String signingInput = h + "." + p;
            String s = signHmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), secretKeyBytes());

            return signingInput + "." + s;
        } catch (Exception e) {
            throw new IllegalStateException("Erreur génération JWT HS256", e);
        }
    }

    /** Signature HMAC-SHA256 (Base64Url) */
    private static String signHmacSha256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return B64URL.encodeToString(mac.doFinal(data));
    }

    /** Dérive la clé HMAC à partir de app.jwt.secret (supporte base64 ou texte brut). */
    private byte[] secretKeyBytes() {
        try {
            return Base64.getDecoder().decode(jwtSecret); // secret fourni en base64
        } catch (IllegalArgumentException e) {
            return jwtSecret.getBytes(StandardCharsets.UTF_8); // sinon texte brut
        }
    }

    /* =========================
       ====== Helpers ==========
       ========================= */

    /** Génère une valeur aléatoire sûre pour refresh (Base64Url, 512 bits). */
    private static String secureRandomToken() {
        byte[] bytes = new byte[64];
        RNG.nextBytes(bytes);
        return B64URL.encodeToString(bytes);
    }

    /** SHA-256 hex pour stocker le hash du refresh en base. */
    private static String sha256Hex(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }
}
