package com.micro.auth.service.impl;

import com.micro.auth.client.AccountClient;
import com.micro.auth.dto.AccountAuthInfo;
import com.micro.auth.dto.DeviceInfo;
import com.micro.auth.dto.LoginRequest;
import com.micro.auth.dto.RefreshTokenRequest;
import com.micro.auth.dto.TokenResponse;
import com.micro.auth.service.iservice.AuthService;
import com.micro.auth.service.iservice.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountClient accountClient;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, DeviceInfo device) {
        AccountAuthInfo acc = accountClient.getByIdentifier(request.usernameOrEmail());

        if (acc == null || acc.id() == null) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }
        if (!"ACTIVE".equalsIgnoreCase(acc.status())) {
            throw new IllegalStateException("Compte non actif.");
        }
        if (!passwordEncoder.matches(request.password(), acc.passwordHash())) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }

        String role = acc.primaryRole() != null ? acc.primaryRole() : "USER";
        return refreshTokenService.issueOnLogin(acc.id(), role, device);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        return refreshTokenService.rotate(request.refreshToken());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    @Override
    @Transactional
    public int logoutAll(java.util.UUID accountId) {
        return refreshTokenService.revokeAllForAccount(accountId);
    }
}

