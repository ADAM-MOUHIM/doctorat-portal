package com.micro.account.mapper;

import com.micro.account.dto.CommonProfileInfoDTO;
import com.micro.account.embedded.CommonProfileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface CommonProfileInfoMapper {

    CommonProfileInfo toEmbedded(CommonProfileInfoDTO dto);

    CommonProfileInfoDTO toDto(CommonProfileInfo embedded);

    void updateEmbedded(CommonProfileInfoDTO dto, @MappingTarget CommonProfileInfo embedded);
}

