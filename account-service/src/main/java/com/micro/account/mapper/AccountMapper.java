package com.micro.account.mapper;

import com.micro.account.dto.AccountResponse;
import com.micro.account.dto.RegisterRequest;
import com.micro.account.entity.Account;
import com.micro.account.enums.AccountStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapStructConfig.class,
        imports = { AccountStatus.class }
)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", expression = "java(AccountStatus.PENDING)")
    @Mapping(target = "profileCompleted", constant = "false")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "version", constant = "0L")
    Account toEntity(RegisterRequest req);

    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    @Mapping(target = "primaryRole", expression = "java(account.getPrimaryRole().name())")
    @Mapping(target = "emailVerified", expression = "java(account.isEmailVerified())")
    AccountResponse toDto(Account account);
}

