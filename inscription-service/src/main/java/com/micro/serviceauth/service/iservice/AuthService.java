package com.micro.serviceauth.service.iservice;

import com.micro.serviceauth.dto.DeviceInfo;
import com.micro.serviceauth.dto.LoginRequest;
import com.micro.serviceauth.dto.RefreshTokenRequest;
import com.micro.serviceauth.dto.TokenResponse;

import java.util.UUID;

/**
 * Contrat du service d'authentification (login, refresh, logout).
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur et émet un couple (access JWT + refresh persistant).
     *
     * @param request   identifiant (username/email) + mot de passe
     * @param device    infos device (user-agent, ip, deviceId) — optionnel
     * @return tokens d’accès et de rafraîchissement
     */
    TokenResponse login(LoginRequest request, DeviceInfo device);

    /**
     * Rotation d'un refresh token (RTR) : renvoie un nouveau couple de tokens.
     *
     * @param request contient le refresh token actuel (en clair)
     * @return nouveau couple de tokens
     */
    TokenResponse refresh(RefreshTokenRequest request);

    /**
     * Révocation d'un refresh token spécifique (logout sur un device).
     *
     * @param refreshToken valeur en clair du refresh token à révoquer
     */
    void logout(String refreshToken);

    /**
     * Révocation de tous les refresh tokens actifs d'un compte (logout all devices).
     *
     * @param accountId identifiant du compte
     * @return nombre de tokens révoqués
     */
    int logoutAll(UUID accountId);
}
