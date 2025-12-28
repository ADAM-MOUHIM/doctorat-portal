package com.devbuild.service.impl;

import com.devbuild.dto.CreateSoutenanceRequest;
import com.devbuild.dto.SoutenanceResponse;
import com.devbuild.dto.UpdateSoutenanceStatusRequest;
import com.devbuild.entity.Soutenance;
import com.devbuild.enums.SoutenanceStatus;
import com.devbuild.mapper.SoutenanceMapper;
import com.devbuild.repository.SoutenanceRepository;
import com.devbuild.service.iservice.SoutenanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SoutenanceServiceImpl implements SoutenanceService {

    private final SoutenanceRepository soutenanceRepository;
    private final SoutenanceMapper soutenanceMapper;

    public SoutenanceServiceImpl(SoutenanceRepository soutenanceRepository,
                                 SoutenanceMapper soutenanceMapper) {
        this.soutenanceRepository = soutenanceRepository;
        this.soutenanceMapper = soutenanceMapper;
    }

    @Override
    public SoutenanceResponse createSoutenance(CreateSoutenanceRequest request) {
        Soutenance entity = soutenanceMapper.toEntity(request);

        // Logique simple de prérequis (tu pourras plus tard appeler d'autres microservices)
        boolean prerequisitesOk =
                (request.publicationsCount() != null && request.publicationsCount() >= 2) &&
                        (request.conferencesCount() != null && request.conferencesCount() >= 2) &&
                        (request.trainingHours() != null && request.trainingHours() >= 200);

        entity.setPrerequisitesValid(prerequisitesOk);
        entity.setStatus(SoutenanceStatus.SUBMITTED); // dès création, on considère "soumise"

        Soutenance saved = soutenanceRepository.save(entity);
        return soutenanceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SoutenanceResponse getById(UUID soutenanceId) {
        Soutenance entity = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new EntityNotFoundException("Soutenance not found"));
        return soutenanceMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoutenanceResponse> getByDoctorant(UUID doctorantAccountId) {
        return soutenanceRepository.findAllByDoctorantAccountId(doctorantAccountId)
                .stream()
                .map(soutenanceMapper::toResponse)
                .toList();
    }

    @Override
    public SoutenanceResponse updateStatus(UUID soutenanceId, UpdateSoutenanceStatusRequest request) {
        Soutenance entity = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new EntityNotFoundException("Soutenance not found"));

        entity.setStatus(request.newStatus());
        // Ici tu peux logguer le commentaire, envoyer un event Kafka, etc.

        return soutenanceMapper.toResponse(entity);
    }

    @Override
    public SoutenanceResponse schedule(UUID soutenanceId, String location, LocalDateTime when) {
        Soutenance entity = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new EntityNotFoundException("Soutenance not found"));

        entity.setStatus(SoutenanceStatus.SCHEDULED);
        entity.setLocation(location);
        entity.setScheduledDateTime(when);

        return soutenanceMapper.toResponse(entity);
    }
}