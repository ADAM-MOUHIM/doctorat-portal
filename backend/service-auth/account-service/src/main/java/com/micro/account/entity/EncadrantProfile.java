package com.micro.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "encadrant_profiles")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_encadrant_profile_base"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncadrantProfile extends BaseProfile {

    @Column(name = "grade", length = 60)
    private String grade;

    @Column(name = "department_id", columnDefinition = "uuid")
    private UUID departmentId;

    @Column(name = "laboratory_id", columnDefinition = "uuid")
    private UUID laboratoryId;
}

