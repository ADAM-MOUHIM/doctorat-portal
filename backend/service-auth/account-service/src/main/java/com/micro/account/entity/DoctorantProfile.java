package com.micro.account.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctorant_profiles")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_doctorant_profile_base"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorantProfile extends BaseProfile {

    @Column(name = "diploma", length = 120)
    private String diploma;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "university", length = 160)
    private String university;
}

