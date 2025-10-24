package com.micro.serviceauth.controller;

import com.micro.serviceauth.dto.*;
import com.micro.serviceauth.service.iservice.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints d'authentification:
 * - /auth/login      : login + émission access/refresh
 * - /auth/refresh    : rotation des refresh tokens
 * - /auth/logout     : révocation d'un refresh (logout device courant)
 * - /auth/logout-all : logout sur tous les devices d'un compte
 *
 * Remarque: DeviceInfo est reconstruit côté backend via User-Agent/IP/Device-Id header.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authentifie un utilisateur (usernameOrEmail + password) et émet les tokens.
     * Headers optionnels:
     *  - X-Device-Id: identifiant applicatif du device (si tu en as un côté front)
     *  - User-Agent : rempli automatiquement par le navigateur
     *  - X-Forwarded-For: IP si reverse-proxy
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest body,
            HttpServletRequest request
    ) {
        DeviceInfo device = extractDeviceInfo(request);
        return ResponseEntity.ok(authService.login(body, device));
    }

    /** Rotation (Refresh Token Rotation): renvoie un nouveau couple access+refresh. */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest body) {
        return ResponseEntity.ok(authService.refresh(body));
    }

    /** Révocation d'un refresh token spécifique (logout du device courant). */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest body) {
        authService.logout(body.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Logout sur tous les devices d'un compte (révoque tous les refresh ACTIFS).
     * NB: en prod, tu peux sécuriser cet endpoint (exiger Bearer) et ignorer le body pour
     * récupérer l'accountId depuis le JWT (claim sub).
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Integer> logoutAll(@RequestParam("accountId") java.util.UUID accountId) {
        int revoked = authService.logoutAll(accountId);
        return ResponseEntity.ok(revoked);
    }

    /* -------------------- helpers -------------------- */

    private static DeviceInfo extractDeviceInfo(HttpServletRequest req) {
        String deviceId  = req.getHeader("X-Device-Id");
        String userAgent = req.getHeader("User-Agent");
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return new DeviceInfo(deviceId, userAgent, ip);
    }
}
