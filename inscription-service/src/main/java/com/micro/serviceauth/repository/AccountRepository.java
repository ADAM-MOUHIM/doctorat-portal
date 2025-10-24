package com.micro.serviceauth.repository;

import com.micro.serviceauth.entity.Account;
import com.micro.serviceauth.enums.AccountStatus;
import com.micro.serviceauth.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * Accès aux comptes.
 * <ul>
 *   <li>Recherche par email / username (insensible à la casse)</li>
 *   <li>Recherche par identifiant libre (username OU email)</li>
 *   <li>Helpers d'existence/compte par rôle (ex. contrôler l'unicité du SUPERUSER au besoin)</li>
 * </ul>
 */
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
}
