package com.micro.account.mapper;

import com.micro.account.dto.CreateEncadrantProfileRequest;
import com.micro.account.dto.EncadrantProfileResponse;
import com.micro.account.dto.UpdateEncadrantProfileRequest;
import com.micro.account.entity.Account;
import com.micro.account.entity.EncadrantProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class, uses = { CommonProfileInfoMapper.class })
public interface EncadrantProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "info", source = "dto.info")
    EncadrantProfile toEntity(CreateEncadrantProfileRequest dto, Account account);

    @Mapping(target = "account", ignore = true)
    void updateEntity(UpdateEncadrantProfileRequest dto, @MappingTarget EncadrantProfile entity);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "info", source = "info")
    EncadrantProfileResponse toDto(EncadrantProfile entity);
}

