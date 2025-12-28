package com.micro.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CommonProfileInfoDTO(
        String firstName,
        String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,
        String address
) { }

