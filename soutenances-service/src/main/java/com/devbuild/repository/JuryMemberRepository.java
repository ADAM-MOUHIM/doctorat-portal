package com.devbuild.repository;

import com.devbuild.entity.JuryMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JuryMemberRepository extends JpaRepository<JuryMember, UUID> {

    List<JuryMember> findAllBySoutenance_Id(UUID soutenanceId);
}