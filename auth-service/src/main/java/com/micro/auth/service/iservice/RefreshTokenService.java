package com.micro.auth.service.iservice;

import com.micro.auth.dto.DeviceInfo;
import com.micro.auth.dto.TokenResponse;

import java.util.UUID;

public interface RefreshTokenService {
    TokenResponse issueOnLogin(UUID accountId, String role, DeviceInfo device);
    TokenResponse rotate(String refreshTokenValue);
    void revoke(String refreshTokenValue);
    int revokeAllForAccount(UUID accountId);
}

