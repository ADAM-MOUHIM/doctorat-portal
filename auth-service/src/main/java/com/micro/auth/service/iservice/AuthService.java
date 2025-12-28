package com.micro.auth.service.iservice;

import com.micro.auth.dto.DeviceInfo;
import com.micro.auth.dto.LoginRequest;
import com.micro.auth.dto.RefreshTokenRequest;
import com.micro.auth.dto.TokenResponse;

import java.util.UUID;

public interface AuthService {
    TokenResponse login(LoginRequest request, DeviceInfo device);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout(String refreshToken);
    int logoutAll(UUID accountId);
}

