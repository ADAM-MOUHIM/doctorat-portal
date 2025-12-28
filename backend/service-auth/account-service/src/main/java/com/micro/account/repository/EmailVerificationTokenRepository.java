package com.micro.account.repository;

import com.micro.account.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByTokenAndUsedAtIsNull(String token);
    void deleteAllByExpiresAtBefore(Instant now);
}
