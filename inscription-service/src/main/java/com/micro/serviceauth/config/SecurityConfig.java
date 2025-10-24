package com.micro.serviceauth.config;

import com.micro.serviceauth.security.JwtAuthFilter;
import com.micro.serviceauth.security.JwtVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configuration Spring Security :
 * - API stateless
 * - /auth/** et /actuator/health en accès public
 * - le reste protégé par Bearer JWT
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtVerifier verifier) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of("*")); // ajuster pour l’URL Angular en prod
                    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setExposedHeaders(List.of("*"));
                    return c;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/auth/login", "/auth/refresh", "/auth/logout", "/auth/logout-all").permitAll()
                        .requestMatchers(HttpMethod.POST, "/accounts").permitAll() // si tu exposes /accounts pour register
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(verifier), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // pour 401 par défaut en JSON si besoin

        return http.build();
    }
}
