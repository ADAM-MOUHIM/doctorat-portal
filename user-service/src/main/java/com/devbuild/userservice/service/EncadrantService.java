package com.micro.accountservice.service;

import com.micro.accountservice.domain.Encadrant;
import com.micro.accountservice.dto.EncadrantRequest;
import com.micro.accountservice.repository.EncadrantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EncadrantService {

    private final EncadrantRepository repository;

    public Encadrant create(EncadrantRequest req) {
        Encadrant e = Encadrant.builder()
                .userId(req.getUserId())
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .departement(req.getDepartement())
                .build();
        return repository.save(e);
    }

    public List<Encadrant> findAll() {
        return repository.findAll();
    }

    public Encadrant findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encadrant not found"));
    }
}
