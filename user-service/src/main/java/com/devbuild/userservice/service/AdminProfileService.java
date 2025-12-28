package com.micro.accountservice.service;

import com.micro.accountservice.domain.AdminProfile;
import com.micro.accountservice.dto.AdminProfileRequest;
import com.micro.accountservice.repository.AdminProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final AdminProfileRepository repository;

    public AdminProfile create(AdminProfileRequest req) {
        AdminProfile a = AdminProfile.builder()
                .userId(req.getUserId())
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .build();
        return repository.save(a);
    }

    public List<AdminProfile> findAll() {
        return repository.findAll();
    }

    public AdminProfile findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    }
}
