package com.devbuild.controller;

import com.devbuild.dto.CreateSoutenanceRequest;
import com.devbuild.dto.SoutenanceResponse;
import com.devbuild.dto.UpdateSoutenanceStatusRequest;
import com.devbuild.service.iservice.SoutenanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/soutenances")
public class SoutenancesController {

    private final SoutenanceService soutenanceService;

    public SoutenancesController(SoutenanceService soutenanceService) {
        this.soutenanceService = soutenanceService;
    }

    @PostMapping
    public ResponseEntity<SoutenanceResponse> create(@Valid @RequestBody CreateSoutenanceRequest request) {
        SoutenanceResponse created = soutenanceService.createSoutenance(request);
        return ResponseEntity
                .created(URI.create("/api/soutenances/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoutenanceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(soutenanceService.getById(id));
    }

    @GetMapping("/by-doctorant/{doctorantAccountId}")
    public ResponseEntity<List<SoutenanceResponse>> getByDoctorant(@PathVariable UUID doctorantAccountId) {
        return ResponseEntity.ok(soutenanceService.getByDoctorant(doctorantAccountId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SoutenanceResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSoutenanceStatusRequest request
    ) {
        return ResponseEntity.ok(soutenanceService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/schedule")
    public ResponseEntity<SoutenanceResponse> schedule(
            @PathVariable UUID id,
            @RequestParam String location,
            @RequestParam LocalDateTime when
    ) {
        return ResponseEntity.ok(soutenanceService.schedule(id, location, when));
    }
}