package com.micro.account.controller;

import com.micro.account.dto.AccountResponse;
import com.micro.account.dto.CreateAdminRequest;
import com.micro.account.service.iservice.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountsController {

    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<List<AccountResponse>> listAll() {
        return ResponseEntity.ok(accountService.listAll());
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<List<AccountResponse>> listAdmins() {
        return ResponseEntity.ok(accountService.listAdmins());
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<AccountResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.ok(accountService.createAdmin(request));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<List<AccountResponse>> listPending() {
        return ResponseEntity.ok(accountService.listPending());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<AccountResponse> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.approve(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<AccountResponse> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.reject(id));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<AccountResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.activateAdmin(id));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('SUPERUSER')")
    public ResponseEntity<AccountResponse> suspend(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.suspendAdmin(id));
    }
}
