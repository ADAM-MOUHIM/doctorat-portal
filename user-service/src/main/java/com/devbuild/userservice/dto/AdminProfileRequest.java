package com.micro.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProfileRequest {
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
}
