package com.devbuild.mapper;

import com.devbuild.dto.CreateSoutenanceRequest;
import com.devbuild.dto.SoutenanceResponse;
import com.devbuild.entity.Soutenance;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class, uses = { JuryMemberMapper.class })
public interface SoutenanceMapper {

    Soutenance toEntity(CreateSoutenanceRequest request);

    SoutenanceResponse toResponse(Soutenance entity);
}
