package com.micro.account.dto;

public record UpdateEncadrantProfileRequest(
        CommonProfileInfoDTO info,
        String grade,
        String departmentId,
        String laboratoryId
) { }
