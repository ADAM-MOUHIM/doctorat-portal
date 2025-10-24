package com.micro.serviceauth.mapper;

import com.micro.serviceauth.dto.CreateDoctorantProfileRequest;
import com.micro.serviceauth.dto.DoctorantProfileResponse;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.entity.DoctorantProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper pour le profil Doctorant (Plan B : @MapsId).
 * Le service charge l'Account et le fournit au mapper pour binder la PK.
 */
@Mapper(config = MapStructConfig.class, uses = { CommonProfileInfoMapper.class })
public interface DoctorantProfileMapper {

    /**
     * Création: on injecte l'Account pour activer @MapsId (profil.id = account.id).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "info", source = "dto.info")
    DoctorantProfile toEntity(CreateDoctorantProfileRequest dto, Account account);

    /**
     * Projection "publique" d'un profil Doctorant.
     */
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "info", source = "info")
    DoctorantProfileResponse toDto(DoctorantProfile entity);
}
