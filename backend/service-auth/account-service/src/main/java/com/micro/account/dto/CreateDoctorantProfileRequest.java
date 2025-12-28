package com.micro.account.dto;

import java.util.UUID;

public record CreateDoctorantProfileRequest(
        UUID accountId,
        CommonProfileInfoDTO info,
        String diploma,
        Integer graduationYear,
        String university
) { }

