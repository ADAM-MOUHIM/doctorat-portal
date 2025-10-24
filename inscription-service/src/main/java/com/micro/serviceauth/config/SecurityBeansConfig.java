package com.micro.serviceauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Déclare les beans de sécurité nécessaires (ici, le PasswordEncoder).
 * <p>Ajoute la dépendance Maven : spring-security-crypto</p>
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Encodeur de mots de passe (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
