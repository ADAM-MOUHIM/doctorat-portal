package com.micro.accountservice.repository;

import com.micro.accountservice.domain.Encadrant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncadrantRepository extends JpaRepository<Encadrant, Long> {
}
