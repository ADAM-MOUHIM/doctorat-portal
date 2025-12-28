package com.micro.accountservice.service;

import com.micro.accountservice.domain.Doctorant;
import com.micro.accountservice.dto.DoctorantRequest;
import com.micro.accountservice.repository.DoctorantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorantService {

    private final DoctorantRepository repository;

    public Doctorant create(DoctorantRequest req) {
        Doctorant d = Doctorant.builder()
                .userId(req.getUserId())
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .cne(req.getCne())
                .sujet(req.getSujet())
                .build();
        return repository.save(d);
    }

    public List<Doctorant> findAll() {
        return repository.findAll();
    }

    public Doctorant findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctorant not found"));
    }
}
