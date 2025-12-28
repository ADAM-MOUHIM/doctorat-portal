package com.micro.account.repository;

import com.micro.account.entity.Account;
import com.micro.account.enums.AccountStatus;
import com.micro.account.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    long countByPrimaryRole(Role role);

    @Query("""
           select a
           from Account a
           where lower(a.username) = lower(:identifier)
              or lower(a.email)    = lower(:identifier)
           """)
    Optional<Account> findByIdentifier(String identifier);

    @Query("select a.status from Account a where a.id = :id")
    AccountStatus getStatusById(UUID id);

    List<Account> findAllByStatus(AccountStatus status);

    List<Account> findAllByPrimaryRole(Role role);
}

