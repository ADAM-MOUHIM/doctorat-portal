package com.micro.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.auth.dto.DeviceInfo;
import com.micro.auth.dto.TokenResponse;
import com.micro.auth.entity.RefreshToken;
import com.micro.auth.enums.RefreshTokenStatus;
import com.micro.auth.repository.RefreshTokenRepository;
import com.micro.auth.service.iservice.RefreshTokenService;
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

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.issuer:serviceAuth}")
    private String jwtIssuer;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration:3600}")
    private long accessTtlSeconds;

    @Value("${app.jwt.refresh-token-expiration:2592000}")
    private long refreshTtlSeconds;

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64URL = Base64.getUrlEncoder().withoutPadding();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    @Transactional
    public TokenResponse issueOnLogin(UUID accountId, String role, DeviceInfo device) {
        Instant now = Instant.now();
        String access = generateAccessJwt(accountId, role, now);

        String refreshPlain = secureRandomToken();
        String refreshHash = sha256Hex(refreshPlain);
        Instant expiresAt = now.plus(refreshTtlSeconds, ChronoUnit.SECONDS);

        RefreshToken entity = RefreshToken.builder()
                .accountId(accountId)
                .accountRole(role)
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

    @Override
    @Transactional
    public TokenResponse rotate(String refreshTokenValue) {
        String hash = sha256Hex(refreshTokenValue);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inconnu."));

        if (current.getStatus() != RefreshTokenStatus.ACTIVE) {
            throw new IllegalStateException("Refresh token inactif.");
        }
        if (current.isExpired()) {
            current.setStatus(RefreshTokenStatus.EXPIRED);
            refreshTokenRepository.save(current);
            throw new IllegalStateException("Refresh token expir?.");
        }

        UUID accountId = current.getAccountId();
        String role = current.getAccountRole();
        Instant now = Instant.now();

        String access = generateAccessJwt(accountId, role, now);

        String newPlain = secureRandomToken();
        String newHash = sha256Hex(newPlain);
        Instant expiresAt = now.plus(refreshTtlSeconds, ChronoUnit.SECONDS);

        RefreshToken next = RefreshToken.builder()
                .accountId(accountId)
                .accountRole(role)
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

        current.setStatus(RefreshTokenStatus.REPLACED);
        current.setReplacedById(next.getId());
        current.setLastUsedAt(now);
        refreshTokenRepository.save(current);

        return new TokenResponse("Bearer", access, accessTtlSeconds, newPlain, refreshTtlSeconds);
    }

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

    @Override
    @Transactional
    public int revokeAllForAccount(UUID accountId) {
        var actives = refreshTokenRepository.findAllByAccountIdAndStatus(accountId, RefreshTokenStatus.ACTIVE);
        Instant now = Instant.now();
        actives.forEach(t -> {
            t.setStatus(RefreshTokenStatus.REVOKED);
            t.setRevokedAt(now);
        });
        refreshTokenRepository.saveAll(actives);
        return actives.size();
    }

    private String generateAccessJwt(UUID accountId, String role, Instant now) {
        try {
            long iat = now.getEpochSecond();
            long exp = now.plusSeconds(accessTtlSeconds).getEpochSecond();

            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", accountId.toString());
            payload.put("role", role);
            payload.put("iss", jwtIssuer);
            payload.put("iat", iat);
            payload.put("exp", exp);

            String h = B64URL.encodeToString(MAPPER.writeValueAsBytes(header));
            String p = B64URL.encodeToString(MAPPER.writeValueAsBytes(payload));
            String signingInput = h + "." + p;
            String s = signHmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), secretKeyBytes());

            return signingInput + "." + s;
        } catch (Exception e) {
            throw new IllegalStateException("Erreur g?n?ration JWT HS256", e);
        }
    }

    private static String signHmacSha256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return B64URL.encodeToString(mac.doFinal(data));
    }

    private byte[] secretKeyBytes() {
        try {
            return Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            return jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String secureRandomToken() {
        byte[] bytes = new byte[64];
        RNG.nextBytes(bytes);
        return B64URL.encodeToString(bytes);
    }

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

