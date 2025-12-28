package com.micro.account.controller;

import com.micro.account.dto.AccountResponse;
import com.micro.account.dto.RegisterRequest;
import com.micro.account.dto.RegisterResponse;
import com.micro.account.service.iservice.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest body) {
        return ResponseEntity.ok(accountService.register(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> me(Principal principal) {
        UUID id = UUID.fromString(principal.getName());
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<AccountResponse> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(accountService.verifyEmail(token));
    }
}
