package com.micro.serviceauth.mapper;

import com.micro.serviceauth.dto.AccountResponse;
import com.micro.serviceauth.dto.RegisterRequest;
import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.enums.AccountStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper pour la création et la projection d'un compte.
 * <p>Note : le hash du mot de passe est géré dans le service (pas ici).</p>
 */
@Mapper(
        config = MapStructConfig.class,
        imports = { AccountStatus.class } // <-- permet d'utiliser AccountStatus.* dans les expressions
)
public interface AccountMapper {

    /**
     * Prépare l'entité Account à partir de la requête d'inscription.
     * <ul>
     *   <li>id ignoré (généré)</li>
     *   <li>passwordHash ignoré (calculé dans le service)</li>
     *   <li>status = PENDING</li>
     *   <li>profileCompleted = false</li>
     *   <li>version = 0</li>
     * </ul>
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", expression = "java(AccountStatus.PENDING)")
    @Mapping(target = "profileCompleted", constant = "false")
    @Mapping(target = "version", constant = "0L") // ⚠ pas "0L", MapStruct parse la constante String
    Account toEntity(RegisterRequest req);

    /**
     * Projection "publique" d'un Account vers AccountResponse.
     */
    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    @Mapping(target = "primaryRole", expression = "java(account.getPrimaryRole().name())")
    AccountResponse toDto(Account account);
}
