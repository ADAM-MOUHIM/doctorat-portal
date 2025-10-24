package com.micro.serviceauth.dto;

/**
 * Contexte device lors de l'émission/usage d'un refresh token.
 * <p>
 * Rempli côté backend à partir des en-têtes (User-Agent, IP) + un deviceId appli si dispo.
 */
public record DeviceInfo(
        String deviceId,
        String userAgent,
        String ipAddress
) { }
