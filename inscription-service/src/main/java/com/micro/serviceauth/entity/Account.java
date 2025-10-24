package com.micro.serviceauth.entity;

import com.micro.serviceauth.enums.AccountStatus;
import com.micro.serviceauth.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_accounts_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_accounts_username", columnNames = "username")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank
    @Column(name = "username", nullable = false, length = 60)
    private String username;

    @Email @NotBlank
    @Column(name = "email", nullable = false, length = 180)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "phone", length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_role", nullable = false, length = 20)
    private Role primaryRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    // Plan B : pas de profileId — le profil partage la même PK que le compte
    @Column(name = "profile_completed", nullable = false)
    @Builder.Default
    private boolean profileCompleted = false;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private long version = 0L;
}
