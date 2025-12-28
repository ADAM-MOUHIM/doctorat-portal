package com.micro.account.dto;

import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        String description
) { }
