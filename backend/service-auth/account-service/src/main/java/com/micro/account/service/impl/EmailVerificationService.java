package com.micro.account.service.impl;

import com.micro.account.entity.Account;
import com.micro.account.entity.EmailVerificationToken;
import com.micro.account.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

    @Value("${app.verification.ttl-minutes:1440}")
    private long ttlMinutes;

    @Transactional
    public String issueToken(Account account) {
        Instant now = Instant.now();
        String token = UUID.randomUUID().toString();

        EmailVerificationToken entity = EmailVerificationToken.builder()
                .token(token)
                .account(account)
                .createdAt(now)
                .expiresAt(now.plusSeconds(ttlMinutes * 60))
                .build();

        tokenRepository.save(entity);
        tokenRepository.deleteAllByExpiresAtBefore(now.minusSeconds(3600)); // cleanup older expired
        return token;
    }

    @Transactional
    public Account verify(String token) {
        EmailVerificationToken evt = tokenRepository.findByTokenAndUsedAtIsNull(token)
                .orElseThrow(() -> new IllegalArgumentException("Token invalide ou déjà utilisé."));

        Instant now = Instant.now();
        if (evt.getExpiresAt().isBefore(now)) {
            throw new IllegalStateException("Token expiré.");
        }

        Account acc = evt.getAccount();
        acc.setEmailVerified(true);
        evt.setUsedAt(now);
        tokenRepository.save(evt);
        return acc;
    }
}
