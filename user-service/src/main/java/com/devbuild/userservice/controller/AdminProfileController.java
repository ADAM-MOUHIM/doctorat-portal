package com.micro.accountservice.controller;

import com.micro.accountservice.domain.AdminProfile;
import com.micro.accountservice.dto.AdminProfileRequest;
import com.micro.accountservice.service.AdminProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService service;

    @PostMapping
    public ResponseEntity<AdminProfile> create(@RequestBody AdminProfileRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<AdminProfile>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProfile> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
