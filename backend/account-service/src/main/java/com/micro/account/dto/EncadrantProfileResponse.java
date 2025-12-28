package com.micro.account.dto;

import java.util.UUID;

public record EncadrantProfileResponse(
        UUID id,
        UUID accountId,
        CommonProfileInfoDTO info,
        String grade,
        UUID departmentId,
        UUID laboratoryId
) { }

