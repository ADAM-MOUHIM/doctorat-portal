package com.micro.account.dto;

import java.util.UUID;

public record LaboratoryResponse(
        UUID id,
        String name,
        String description
) { }
