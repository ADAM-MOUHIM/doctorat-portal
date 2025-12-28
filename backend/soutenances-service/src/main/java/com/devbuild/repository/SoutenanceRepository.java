package com.devbuild.repository;

import com.devbuild.entity.Soutenance;
import com.devbuild.enums.SoutenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SoutenanceRepository extends JpaRepository<Soutenance, UUID> {

    List<Soutenance> findAllByDoctorantAccountId(UUID doctorantAccountId);

    List<Soutenance> findAllByStatus(SoutenanceStatus status);
}