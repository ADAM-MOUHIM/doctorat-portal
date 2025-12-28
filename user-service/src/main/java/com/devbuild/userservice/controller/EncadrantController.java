package com.micro.accountservice.controller;

import com.micro.accountservice.domain.Encadrant;
import com.micro.accountservice.dto.EncadrantRequest;
import com.micro.accountservice.service.EncadrantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encadrants")
@RequiredArgsConstructor
public class EncadrantController {

    private final EncadrantService service;

    @PostMapping
    public ResponseEntity<Encadrant> create(@RequestBody EncadrantRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<Encadrant>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encadrant> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
