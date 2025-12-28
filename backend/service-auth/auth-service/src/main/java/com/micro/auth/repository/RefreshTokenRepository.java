package com.micro.auth.repository;

import com.micro.auth.entity.RefreshToken;
import com.micro.auth.enums.RefreshTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByAccountIdAndStatus(UUID accountId, RefreshTokenStatus status);

    List<RefreshToken> findAllByFamilyId(UUID familyId);
}

