package com.micro.serviceauth.controller;

import com.micro.serviceauth.dto.AccountResponse;
import com.micro.serviceauth.dto.RegisterRequest;
import com.micro.serviceauth.dto.RegisterResponse;
import com.micro.serviceauth.service.iservice.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

/**
 * Endpoints comptes:
 * - POST /accounts       : inscription
 * - GET  /accounts/{id}  : lecture d'un compte
 * - GET  /accounts/me    : lecture du compte courant via JWT (claim 'sub')
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;

    /** Inscription d'un compte (status PENDING par défaut). */
    @PostMapping
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest body) {
        return ResponseEntity.ok(accountService.register(body));
    }

    /** Lecture d'un compte par id. */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    /**
     * Lecture du compte courant (id extrait du JWT → principal.getName()).
     * SecurityConfig/Filter place 'sub' (UUID du compte) comme principal.
     */
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> me(Principal principal) {
        UUID id = UUID.fromString(principal.getName());
        return ResponseEntity.ok(accountService.getById(id));
    }
}
