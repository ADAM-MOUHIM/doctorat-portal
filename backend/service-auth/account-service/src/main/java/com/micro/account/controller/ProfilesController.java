package com.micro.account.controller;

import com.micro.account.dto.*;
import com.micro.account.service.iservice.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {

    private final ProfileService profileService;

    @PostMapping("/doctorant")
    public ResponseEntity<DoctorantProfileResponse> createDoctorant(
            @Valid @RequestBody CreateDoctorantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.createDoctorantProfile(body));
    }

    @PostMapping("/encadrant")
    public ResponseEntity<EncadrantProfileResponse> createEncadrant(
            @Valid @RequestBody CreateEncadrantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.createEncadrantProfile(body));
    }

    @GetMapping("/doctorant/{accountId}")
    public ResponseEntity<DoctorantProfileResponse> getDoctorant(@PathVariable UUID accountId) {
        return ResponseEntity.ok(profileService.getDoctorantProfile(accountId));
    }

    @GetMapping("/encadrant/{accountId}")
    public ResponseEntity<EncadrantProfileResponse> getEncadrant(@PathVariable UUID accountId) {
        return ResponseEntity.ok(profileService.getEncadrantProfile(accountId));
    }

    @PutMapping("/doctorant/{accountId}")
    public ResponseEntity<DoctorantProfileResponse> updateDoctorant(
            @PathVariable UUID accountId,
            @Valid @RequestBody UpdateDoctorantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.updateDoctorantProfile(accountId, body));
    }

    @PutMapping("/encadrant/{accountId}")
    public ResponseEntity<EncadrantProfileResponse> updateEncadrant(
            @PathVariable UUID accountId,
            @Valid @RequestBody UpdateEncadrantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.updateEncadrantProfile(accountId, body));
    }
}

