package com.micro.accountservice.controller;

import com.micro.accountservice.domain.Doctorant;
import com.micro.accountservice.dto.DoctorantRequest;
import com.micro.accountservice.service.DoctorantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctorants")
@RequiredArgsConstructor
public class DoctorantController {

    private final DoctorantService service;

    @PostMapping
    public ResponseEntity<Doctorant> create(@RequestBody DoctorantRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<Doctorant>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctorant> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
