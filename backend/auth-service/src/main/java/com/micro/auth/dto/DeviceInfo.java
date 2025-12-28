package com.micro.auth.dto;

public record DeviceInfo(
        String deviceId,
        String userAgent,
        String ipAddress
) { }

