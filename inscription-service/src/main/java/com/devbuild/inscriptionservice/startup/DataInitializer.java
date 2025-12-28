package com.micro.authservice.startup;

import com.micro.authservice.domain.Role;
import com.micro.authservice.domain.RoleName;
import com.micro.authservice.domain.User;
import com.micro.authservice.repository.RoleRepository;
import com.micro.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // Ensure roles
        for (RoleName rn : RoleName.values()) {
            roleRepository.findByName(rn).orElseGet(() -> {
                log.info("Creating role {}", rn);
                return roleRepository.save(Role.builder().name(rn).build());
            });
        }

        // Root
        if (userRepository.findByUsername("root").isEmpty()) {
            log.info("Creating ROOT user");
            createUser("root", "root@univ.local", "Root#123", RoleName.ROOT);
        }

        // Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating ADMIN user");
            createUser("admin", "admin@univ.local", "Admin#123", RoleName.ADMIN);
        }
    }

    private void createUser(String username, String email, String rawPassword, RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found " + roleName));

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .enabled(true)
                .locked(false)
                .build();
        user.getRoles().add(role);

        userRepository.save(user);
    }
}
