package com.micro.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartmentRequest(
        @NotBlank @Size(min = 2, max = 120) String name,
        @Size(max = 255) String description
) { }
