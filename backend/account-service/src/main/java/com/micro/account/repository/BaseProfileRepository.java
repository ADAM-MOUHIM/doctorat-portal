package com.micro.account.repository;

import com.micro.account.entity.BaseProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BaseProfileRepository extends JpaRepository<BaseProfile, UUID> {
    Optional<BaseProfile> findByAccount_Id(UUID accountId);
    boolean existsByAccount_Id(UUID accountId);
}

