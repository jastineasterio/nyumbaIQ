package com.nyumbaiq.backend.config;

import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User owner = User.builder()
                        .firstName("System")
                        .middleName(null)
                        .lastName("Owner")
                        .email("owner@nyumbaiq.com")
                        .phone("+255712345678")
                        .username("owner")
                        .passwordHash(passwordEncoder.encode("Owner123!"))
                        .role(Role.OWNER)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(owner);
            }
        };
    }
}
