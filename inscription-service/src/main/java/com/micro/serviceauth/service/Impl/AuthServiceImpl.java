package com.micro.serviceauth.service.Impl;

import com.micro.serviceauth.dto.DeviceInfo;
import com.micro.serviceauth.dto.LoginRequest;
import com.micro.serviceauth.dto.RefreshTokenRequest;
import com.micro.serviceauth.dto.TokenResponse;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.enums.AccountStatus;
import com.micro.serviceauth.repository.AccountRepository;
import com.micro.serviceauth.service.iservice.AuthService;
import com.micro.serviceauth.service.iservice.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service d'authentification (login/refresh/logout).
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, DeviceInfo device) {
        Account acc = accountRepository.findByIdentifier(request.usernameOrEmail())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides."));

        if (acc.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Compte non actif.");
        }
        if (!passwordEncoder.matches(request.password(), acc.getPasswordHash())) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }

        return refreshTokenService.issueOnLogin(acc, device);
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
