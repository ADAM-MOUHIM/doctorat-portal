package com.micro.account.service.impl;

import com.micro.account.dto.*;
import com.micro.account.embedded.CommonProfileInfo;
import com.micro.account.entity.Account;
import com.micro.account.entity.DoctorantProfile;
import com.micro.account.entity.EncadrantProfile;
import com.micro.account.enums.Role;
import com.micro.account.mapper.DoctorantProfileMapper;
import com.micro.account.mapper.EncadrantProfileMapper;
import com.micro.account.repository.AccountRepository;
import com.micro.account.repository.DoctorantProfileRepository;
import com.micro.account.repository.EncadrantProfileRepository;
import com.micro.account.service.iservice.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
            throw new IllegalStateException("Role incompatible: DOCTORANT requis.");
        }
        if (doctorantProfileRepository.existsByAccount_Id(acc.getId())) {
            throw new IllegalStateException("Profil doctorant deja existant.");
        }

        DoctorantProfile entity = doctorantProfileMapper.toEntity(request, acc);
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
            throw new IllegalStateException("Role incompatible: DIRECTEUR requis.");
        }
        if (encadrantProfileRepository.existsByAccount_Id(acc.getId())) {
            throw new IllegalStateException("Profil encadrant deja existant.");
        }

        EncadrantProfile entity = encadrantProfileMapper.toEntity(request, acc);
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

    @Override
    @Transactional
    public DoctorantProfileResponse updateDoctorantProfile(UUID accountId, UpdateDoctorantProfileRequest request) {
        DoctorantProfile entity = doctorantProfileRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profil doctorant introuvable."));

        if (entity.getAccount().getPrimaryRole() != Role.DOCTORANT) {
            throw new IllegalStateException("Role incompatible: DOCTORANT requis.");
        }
        if (entity.getInfo() == null) {
            entity.setInfo(new CommonProfileInfo());
        }

        doctorantProfileMapper.updateEntity(request, entity);
        entity = doctorantProfileRepository.save(entity);

        Account acc = entity.getAccount();
        if (!acc.isProfileCompleted()) {
            acc.setProfileCompleted(true);
            accountRepository.save(acc);
        }

        return doctorantProfileMapper.toDto(entity);
    }

    @Override
    @Transactional
    public EncadrantProfileResponse updateEncadrantProfile(UUID accountId, UpdateEncadrantProfileRequest request) {
        EncadrantProfile entity = encadrantProfileRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profil encadrant introuvable."));

        if (entity.getAccount().getPrimaryRole() != Role.DIRECTEUR) {
            throw new IllegalStateException("Role incompatible: DIRECTEUR requis.");
        }
        if (entity.getInfo() == null) {
            entity.setInfo(new CommonProfileInfo());
        }

        encadrantProfileMapper.updateEntity(request, entity);
        entity = encadrantProfileRepository.save(entity);

        Account acc = entity.getAccount();
        if (!acc.isProfileCompleted()) {
            acc.setProfileCompleted(true);
            accountRepository.save(acc);
        }

        return encadrantProfileMapper.toDto(entity);
    }
}

