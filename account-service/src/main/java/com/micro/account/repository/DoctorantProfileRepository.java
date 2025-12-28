package com.micro.account.repository;

import com.micro.account.entity.DoctorantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorantProfileRepository extends JpaRepository<DoctorantProfile, UUID> {
    Optional<DoctorantProfile> findByAccount_Id(UUID accountId);
    boolean existsByAccount_Id(UUID accountId);
}

