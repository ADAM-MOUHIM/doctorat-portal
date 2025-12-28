package com.micro.account.repository;

import com.micro.account.entity.EncadrantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EncadrantProfileRepository extends JpaRepository<EncadrantProfile, UUID> {
    Optional<EncadrantProfile> findByAccount_Id(UUID accountId);
    boolean existsByAccount_Id(UUID accountId);
    List<EncadrantProfile> findAllByDepartmentId(UUID departmentId);
    List<EncadrantProfile> findAllByLaboratoryId(UUID laboratoryId);
}

