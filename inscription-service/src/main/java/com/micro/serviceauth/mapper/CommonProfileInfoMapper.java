package com.micro.serviceauth.mapper;

import com.micro.serviceauth.embedded.CommonProfileInfo;
import com.micro.serviceauth.dto.CommonProfileInfoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper des informations communes de profil (embeddable <-> DTO).
 */
@Mapper(config = MapStructConfig.class)
public interface CommonProfileInfoMapper {

    /**
     * Mappe un DTO vers l'embeddable JPA.
     */
    CommonProfileInfo toEmbedded(CommonProfileInfoDTO dto);

    /**
     * Mappe l'embeddable JPA vers le DTO.
     */
    CommonProfileInfoDTO toDto(CommonProfileInfo embedded);
}
