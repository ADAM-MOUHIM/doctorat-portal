package com.micro.serviceauth.service.iservice;

import com.micro.serviceauth.dto.DeviceInfo;
import com.micro.serviceauth.dto.TokenResponse;
import com.micro.serviceauth.entity.Account;

import java.util.UUID;

/**
 * Contrat de gestion Pro+ des refresh tokens persistés et de l'émission JWT HS256.
 */
public interface RefreshTokenService {

    /**
     * Émet les tokens après login réussi.
     */
    TokenResponse issueOnLogin(Account account, DeviceInfo device);

    /**
     * Effectue une rotation et renvoie les nouveaux tokens.
     */
    TokenResponse rotate(String refreshTokenValue);

    /**
     * Révoque un refresh token (logout device).
     */
    void revoke(String refreshTokenValue);

    /**
     * Révoque tous les refresh tokens actifs d'un compte (logout all).
     *
     * @return nombre de tokens révoqués
     */
    int revokeAllForAccount(UUID accountId);
}
