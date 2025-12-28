package com.devbuild.mapper;

import com.devbuild.dto.JuryMemberRequest;
import com.devbuild.dto.JuryMemberResponse;
import com.devbuild.entity.JuryMember;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface JuryMemberMapper {

    // MapStruct va remplir ce qu’il peut (fullName, email, institution, role, external)
    JuryMember toEntity(JuryMemberRequest request);

    JuryMemberResponse toResponse(JuryMember entity);
}