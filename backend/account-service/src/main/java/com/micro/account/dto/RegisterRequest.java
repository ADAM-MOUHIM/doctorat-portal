package com.micro.account.dto;

import com.micro.account.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 60) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        String phone,
        Role primaryRole
) { }

