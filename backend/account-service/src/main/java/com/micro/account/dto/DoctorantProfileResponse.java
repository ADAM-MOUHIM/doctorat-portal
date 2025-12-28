package com.micro.account.dto;

import java.util.UUID;

public record DoctorantProfileResponse(
        UUID id,
        UUID accountId,
        CommonProfileInfoDTO info,
        String diploma,
        Integer graduationYear,
        String university
) { }

