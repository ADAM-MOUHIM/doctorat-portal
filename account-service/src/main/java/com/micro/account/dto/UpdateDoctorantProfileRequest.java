package com.micro.account.dto;

public record UpdateDoctorantProfileRequest(
        CommonProfileInfoDTO info,
        String diploma,
        Integer graduationYear,
        String university
) { }
