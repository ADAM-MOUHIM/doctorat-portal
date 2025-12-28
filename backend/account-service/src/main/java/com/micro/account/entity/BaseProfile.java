package com.micro.account.entity;

import com.micro.account.embedded.CommonProfileInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "profiles")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseProfile {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_profiles_account"))
    private Account account;

    @Embedded
    private CommonProfileInfo info;
}

