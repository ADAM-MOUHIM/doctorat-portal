package com.micro.serviceauth.service.Impl;

import com.micro.serviceauth.dto.AccountResponse;
import com.micro.serviceauth.dto.RegisterRequest;
import com.micro.serviceauth.dto.RegisterResponse;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.enums.AccountStatus;
import com.micro.serviceauth.mapper.AccountMapper;
import com.micro.serviceauth.repository.AccountRepository;
import com.micro.serviceauth.service.iservice.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implémentation du service de gestion des comptes.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Unicité
        if (accountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalStateException("Email déjà utilisé.");
        }
        if (accountRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalStateException("Username déjà utilisé.");
        }

        // MapStruct -> entité (status=PENDING, version=0, profileCompleted=false)
        Account entity = accountMapper.toEntity(request);
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setStatus(AccountStatus.ACTIVE);// matttnssahshshshshshshshhshshshhshshshhs
        // Si tu veux imposer PENDING au register, laisse tel quel.
        // Si SUPERUSER doit être unique, gère-le ici (countByPrimaryRole).

        Account saved = accountRepository.save(entity);

        // Réponse "publique"
        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getStatus().name(),
                saved.getPrimaryRole().name(),
                saved.isProfileCompleted()
        );
    }

    @Override
    public AccountResponse getById(UUID id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        return accountMapper.toDto(acc);
    }
}
