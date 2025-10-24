package com.micro.serviceauth.mapper;

import com.micro.serviceauth.dto.CreateEncadrantProfileRequest;
import com.micro.serviceauth.dto.EncadrantProfileResponse;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.entity.EncadrantProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper pour le profil Encadrant/Directeur (Plan B : @MapsId).
 */
@Mapper(config = MapStructConfig.class, uses = { CommonProfileInfoMapper.class })
public interface EncadrantProfileMapper {

    /**
     * Création: on injecte l'Account pour activer @MapsId (profil.id = account.id).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "info", source = "dto.info")
    EncadrantProfile toEntity(CreateEncadrantProfileRequest dto, Account account);

    /**
     * Projection "publique" d'un profil Encadrant.
     */
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "info", source = "info")
    EncadrantProfileResponse toDto(EncadrantProfile entity);
}
