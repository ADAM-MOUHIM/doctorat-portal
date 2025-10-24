package com.micro.serviceauth.enums;

/**
 * Statut d'un refresh token persistant.
 * <ul>
 *   <li>ACTIVE    : utilisable pour refresh</li>
 *   <li>REVOKED   : explicitement révoqué (logout, admin)</li>
 *   <li>REPLACED  : tournant/rotation effectuée (chaîne de tokens)</li>
 *   <li>EXPIRED   : dépassé (techniquement déduit d'expiresAt, mais utile pour reporting)</li>
 * </ul>
 */
public enum RefreshTokenStatus {
    ACTIVE, REVOKED, REPLACED, EXPIRED
}
