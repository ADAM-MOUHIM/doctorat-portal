package com.micro.serviceauth.repository;

import com.micro.serviceauth.entity.EncadrantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Accès aux profils Encadrant/Directeur.
 * PK = accountId (Plan B @MapsId).
 */
public interface EncadrantProfileRepository extends JpaRepository<EncadrantProfile, UUID> {

    Optional<EncadrantProfile> findByAccount_Id(UUID accountId);

    boolean existsByAccount_Id(UUID accountId);

    /**
     * Exemple de filtres utiles si tu alimentes un annuaire :
     * récupérer les encadrants d'un département ou d'un labo.
     */
    List<EncadrantProfile> findAllByDepartmentId(UUID departmentId);

    List<EncadrantProfile> findAllByLaboratoryId(UUID laboratoryId);
}
