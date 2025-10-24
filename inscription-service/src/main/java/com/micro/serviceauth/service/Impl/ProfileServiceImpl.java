package com.micro.serviceauth.service.Impl;

import com.micro.serviceauth.dto.*;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.entity.DoctorantProfile;
import com.micro.serviceauth.entity.EncadrantProfile;
import com.micro.serviceauth.enums.Role;
import com.micro.serviceauth.mapper.DoctorantProfileMapper;
import com.micro.serviceauth.mapper.EncadrantProfileMapper;
import com.micro.serviceauth.repository.AccountRepository;
import com.micro.serviceauth.repository.DoctorantProfileRepository;
import com.micro.serviceauth.repository.EncadrantProfileRepository;
import com.micro.serviceauth.service.iservice.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implémentation du service de gestion des profils (Doctorant / Encadrant).
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepository;
    private final DoctorantProfileRepository doctorantProfileRepository;
    private final EncadrantProfileRepository encadrantProfileRepository;
    private final DoctorantProfileMapper doctorantProfileMapper;
    private final EncadrantProfileMapper encadrantProfileMapper;

    @Override
    @Transactional
    public DoctorantProfileResponse createDoctorantProfile(CreateDoctorantProfileRequest request) {
        Account acc = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));

        if (acc.getPrimaryRole() != Role.DOCTORANT) {
            throw new IllegalStateException("Rôle incompatible: DOCTORANT requis.");
        }
        if (doctorantProfileRepository.existsByAccount_Id(acc.getId())) {
            throw new IllegalStateException("Profil doctorant déjà existant.");
        }

        DoctorantProfile entity = doctorantProfileMapper.toEntity(request, acc); // @MapsId
        entity = doctorantProfileRepository.save(entity);

        acc.setProfileCompleted(true);
        accountRepository.save(acc);

        return doctorantProfileMapper.toDto(entity);
    }

    @Override
    @Transactional
    public EncadrantProfileResponse createEncadrantProfile(CreateEncadrantProfileRequest request) {
        Account acc = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));

        if (acc.getPrimaryRole() != Role.DIRECTEUR) {
            throw new IllegalStateException("Rôle incompatible: DIRECTEUR requis.");
        }
        if (encadrantProfileRepository.existsByAccount_Id(acc.getId())) {
            throw new IllegalStateException("Profil encadrant déjà existant.");
        }

        EncadrantProfile entity = encadrantProfileMapper.toEntity(request, acc); // @MapsId
        entity = encadrantProfileRepository.save(entity);

        acc.setProfileCompleted(true);
        accountRepository.save(acc);

        return encadrantProfileMapper.toDto(entity);
    }

    @Override
    public DoctorantProfileResponse getDoctorantProfile(UUID accountId) {
        DoctorantProfile entity = doctorantProfileRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profil doctorant introuvable."));
        return doctorantProfileMapper.toDto(entity);
    }

    @Override
    public EncadrantProfileResponse getEncadrantProfile(UUID accountId) {
        EncadrantProfile entity = encadrantProfileRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profil encadrant introuvable."));
        return encadrantProfileMapper.toDto(entity);
    }
}
