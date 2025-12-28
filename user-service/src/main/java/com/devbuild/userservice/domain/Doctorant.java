package com.micro.accountservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctorants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctorant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // id of auth-service user

    private String nom;
    private String prenom;
    private String email;
    private String cne;
    private String sujet;
}
