package com.micro.authservice.repository;

import com.micro.authservice.domain.Role;
import com.micro.authservice.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
