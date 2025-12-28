package com.micro.account.mapper;

import com.micro.account.dto.CreateDoctorantProfileRequest;
import com.micro.account.dto.DoctorantProfileResponse;
import com.micro.account.dto.UpdateDoctorantProfileRequest;
import com.micro.account.entity.Account;
import com.micro.account.entity.DoctorantProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class, uses = { CommonProfileInfoMapper.class })
public interface DoctorantProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "info", source = "dto.info")
    DoctorantProfile toEntity(CreateDoctorantProfileRequest dto, Account account);

    @Mapping(target = "account", ignore = true)
    void updateEntity(UpdateDoctorantProfileRequest dto, @MappingTarget DoctorantProfile entity);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "info", source = "info")
    DoctorantProfileResponse toDto(DoctorantProfile entity);
}

