package com.micro.serviceauth.repository;

import com.micro.serviceauth.entity.DoctorantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Accès aux profils Doctorant.
 * PK = accountId (Plan B @MapsId).
 */
public interface DoctorantProfileRepository extends JpaRepository<DoctorantProfile, UUID> {

    Optional<DoctorantProfile> findByAccount_Id(UUID accountId);

    boolean existsByAccount_Id(UUID accountId);
}
