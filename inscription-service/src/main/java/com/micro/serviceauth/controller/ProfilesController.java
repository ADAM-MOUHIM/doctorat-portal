package com.micro.serviceauth.controller;

import com.micro.serviceauth.dto.*;
import com.micro.serviceauth.service.iservice.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoints profils (Plan B @MapsId):
 * - POST /profiles/doctorant
 * - POST /profiles/encadrant
 * - GET  /profiles/doctorant/{accountId}
 * - GET  /profiles/encadrant/{accountId}
 */
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfilesController {

    private final ProfileService profileService;

    /** Création du profil Doctorant pour un compte (role DOCTORANT requis). */
    @PostMapping("/doctorant")
    public ResponseEntity<DoctorantProfileResponse> createDoctorant(
            @Valid @RequestBody CreateDoctorantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.createDoctorantProfile(body));
    }

    /** Création du profil Encadrant pour un compte (role DIRECTEUR requis). */
    @PostMapping("/encadrant")
    public ResponseEntity<EncadrantProfileResponse> createEncadrant(
            @Valid @RequestBody CreateEncadrantProfileRequest body
    ) {
        return ResponseEntity.ok(profileService.createEncadrantProfile(body));
    }

    /** Lecture profil Doctorant par accountId (PK partagée). */
    @GetMapping("/doctorant/{accountId}")
    public ResponseEntity<DoctorantProfileResponse> getDoctorant(@PathVariable UUID accountId) {
        return ResponseEntity.ok(profileService.getDoctorantProfile(accountId));
    }

    /** Lecture profil Encadrant par accountId (PK partagée). */
    @GetMapping("/encadrant/{accountId}")
    public ResponseEntity<EncadrantProfileResponse> getEncadrant(@PathVariable UUID accountId) {
        return ResponseEntity.ok(profileService.getEncadrantProfile(accountId));
    }
}
