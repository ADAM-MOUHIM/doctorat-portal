package com.micro.account.service.impl;

import com.micro.account.dto.AccountResponse;
import com.micro.account.dto.CreateAdminRequest;
import com.micro.account.dto.RegisterRequest;
import com.micro.account.dto.RegisterResponse;
import com.micro.account.entity.Account;
import com.micro.account.enums.AccountStatus;
import com.micro.account.enums.Role;
import com.micro.account.mapper.AccountMapper;
import com.micro.account.repository.AccountRepository;
import com.micro.account.service.iservice.AccountService;
import com.micro.account.service.mail.EmailNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailNotificationService emailNotificationService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (request.primaryRole() == null) {
            throw new IllegalArgumentException("primaryRole obligatoire.");
        }

        boolean callerIsSuperuser = currentUserIsSuperuser();

        // Un seul SUPERUSER possible
        if (request.primaryRole() == Role.SUPERUSER
                && accountRepository.countByPrimaryRole(Role.SUPERUSER) > 0) {
            throw new IllegalStateException("Un SUPERUSER existe deja.");
        }

        // Seul le SUPERUSER peut creer un ADMIN
        if (request.primaryRole() == Role.ADMIN && !callerIsSuperuser) {
            throw new AccessDeniedException("Seul le SUPERUSER peut creer un compte ADMIN.");
        }

        Optional<Account> existingByEmail = accountRepository.findByEmailIgnoreCase(request.email());
        Optional<Account> existingByUsername = accountRepository.findByUsernameIgnoreCase(request.username());

        Account reusable = null;
        if (existingByEmail.isPresent() && existingByEmail.get().getStatus() == AccountStatus.REJECTED) {
            reusable = existingByEmail.get();
        }
        if (existingByUsername.isPresent() && existingByUsername.get().getStatus() == AccountStatus.REJECTED) {
            if (reusable != null && !reusable.getId().equals(existingByUsername.get().getId())) {
                throw new IllegalStateException("Email/Username deja utilises.");
            }
            reusable = existingByUsername.get();
        }

        if (existingByEmail.isPresent() && existingByEmail.get().getStatus() != AccountStatus.REJECTED
                && (reusable == null || !reusable.getId().equals(existingByEmail.get().getId()))) {
            throw new IllegalStateException("Email deja utilise.");
        }
        if (existingByUsername.isPresent() && existingByUsername.get().getStatus() != AccountStatus.REJECTED
                && (reusable == null || !reusable.getId().equals(existingByUsername.get().getId()))) {
            throw new IllegalStateException("Username deja utilise.");
        }

        Account entity = reusable != null ? reusable : accountMapper.toEntity(request);
        entity.setUsername(request.username());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setPrimaryRole(request.primaryRole());
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setProfileCompleted(false);
        entity.setEmailVerified(false);
        if (request.primaryRole() == Role.SUPERUSER || (request.primaryRole() == Role.ADMIN && callerIsSuperuser)) {
            entity.setStatus(AccountStatus.ACTIVE);
            entity.setEmailVerified(true);
        } else {
            entity.setStatus(AccountStatus.PENDING);
        }

        Account saved = accountRepository.save(entity);

        if (!saved.isEmailVerified()) {
            String token = emailVerificationService.issueToken(saved);
            emailNotificationService.sendVerification(saved, token);
        }

        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getStatus().name(),
                saved.getPrimaryRole().name(),
                saved.isProfileCompleted(),
                saved.isEmailVerified()
        );
    }

    @Override
    @Transactional
    public AccountResponse createAdmin(CreateAdminRequest request) {
        RegisterResponse created = register(new RegisterRequest(
                request.username(),
                request.email(),
                request.password(),
                request.phone(),
                Role.ADMIN
        ));

        Optional<Account> admin = accountRepository.findById(created.accountId());
        return admin.map(accountMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Admin cree introuvable."));
    }

    @Override
    public List<AccountResponse> listPending() {
        return accountRepository.findAllByStatus(AccountStatus.PENDING)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse approve(UUID id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        if (!acc.isEmailVerified()) {
            throw new IllegalStateException("Email non verifie pour ce compte.");
        }
        acc.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(acc);
        emailNotificationService.sendApproval(acc);
        return accountMapper.toDto(acc);
    }

    @Override
    @Transactional
    public AccountResponse reject(UUID id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        acc.setStatus(AccountStatus.REJECTED);
        accountRepository.save(acc);
        emailNotificationService.sendRejection(acc);
        return accountMapper.toDto(acc);
    }

    private boolean currentUserIsSuperuser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_SUPERUSER".equalsIgnoreCase(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AccountResponse getById(UUID id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        return accountMapper.toDto(acc);
    }

    @Override
    public List<AccountResponse> listAdmins() {
        return accountRepository.findAllByPrimaryRole(Role.ADMIN)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public List<AccountResponse> listAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse activateAdmin(UUID id) {
        Account admin = loadAdminOrThrow(id);
        admin.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(admin);
        return accountMapper.toDto(admin);
    }

    @Override
    @Transactional
    public AccountResponse suspendAdmin(UUID id) {
        Account admin = loadAdminOrThrow(id);
        admin.setStatus(AccountStatus.SUSPENDED);
        accountRepository.save(admin);
        return accountMapper.toDto(admin);
    }

    @Override
    @Transactional
    public AccountResponse verifyEmail(String token) {
        Account acc = emailVerificationService.verify(token);
        accountRepository.save(acc);
        return accountMapper.toDto(acc);
    }

    private Account loadAdminOrThrow(UUID id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        if (acc.getPrimaryRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Seuls les comptes ADMIN peuvent etre modifies ici.");
        }
        return acc;
    }
}

