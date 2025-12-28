package com.devbuild.entity;

import com.devbuild.enums.JuryRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "jury_members")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JuryMember {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "soutenance_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_jury_soutenance")
    )
    private Soutenance soutenance;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "email", length = 160)
    private String email;

    @Column(name = "institution", length = 200)
    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private JuryRole role;

    @Column(name = "external_member", nullable = false)
    @Builder.Default
    private boolean external = false;
}
