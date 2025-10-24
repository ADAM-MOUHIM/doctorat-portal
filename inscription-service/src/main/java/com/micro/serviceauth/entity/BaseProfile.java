package com.micro.serviceauth.entity;

import com.micro.serviceauth.embedded.CommonProfileInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "profiles")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class BaseProfile {

    // PK du profil = PK du compte (pas de @GeneratedValue)
    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    // OneToOne avec clé primaire partagée
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_profiles_account"))
    private Account account;

    // Champs communs
    @Embedded
    private CommonProfileInfo info;
}
