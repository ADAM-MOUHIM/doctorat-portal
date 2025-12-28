package com.micro.accountservice.repository;

import com.micro.accountservice.domain.Doctorant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorantRepository extends JpaRepository<Doctorant, Long> {
}
