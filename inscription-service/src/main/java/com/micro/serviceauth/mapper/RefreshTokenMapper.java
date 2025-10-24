package com.micro.serviceauth.mapper;

import com.micro.serviceauth.dto.RefreshTokenResponse;
import com.micro.serviceauth.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mappeur entity -> DTO pour l'affichage des refresh tokens côté front.
 */
@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    RefreshTokenResponse toDto(RefreshToken entity);
}
