package com.micro.auth.controller;

import com.micro.auth.dto.*;
import com.micro.auth.service.iservice.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest body,
            HttpServletRequest request
    ) {
        DeviceInfo device = extractDeviceInfo(request);
        return ResponseEntity.ok(authService.login(body, device));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest body) {
        return ResponseEntity.ok(authService.refresh(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest body) {
        authService.logout(body.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Integer> logoutAll(@RequestParam("accountId") java.util.UUID accountId) {
        int revoked = authService.logoutAll(accountId);
        return ResponseEntity.ok(revoked);
    }

    private static DeviceInfo extractDeviceInfo(HttpServletRequest req) {
        String deviceId  = req.getHeader("X-Device-Id");
        String userAgent = req.getHeader("User-Agent");
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return new DeviceInfo(deviceId, userAgent, ip);
    }
}

