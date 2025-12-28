package com.micro.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorantRequest {
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String cne;
    private String sujet;
}
