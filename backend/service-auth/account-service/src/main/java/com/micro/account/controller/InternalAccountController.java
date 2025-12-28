package com.micro.account.controller;

import com.micro.account.dto.AccountAuthInfoResponse;
import com.micro.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/identifier/{identifier}")
    public ResponseEntity<AccountAuthInfoResponse> getByIdentifier(@PathVariable String identifier) {
        var acc = accountRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        return ResponseEntity.ok(new AccountAuthInfoResponse(
                acc.getId(),
                acc.getUsername(),
                acc.getEmail(),
                acc.getPasswordHash(),
                acc.getStatus().name(),
                acc.getPrimaryRole().name()
        ));
    }
}

