package com.micro.account.repository;

import com.micro.account.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LaboratoryRepository extends JpaRepository<Laboratory, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Laboratory> findByNameIgnoreCase(String name);
}
