package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.RefreshTokenRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.LoginRequest;
import com.nyumbaiq.backend.dto.TokenResponse;
import com.nyumbaiq.backend.exception.BadRequestException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.security.JwtService;
import com.nyumbaiq.backend.service.RabbitMqProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrentUser currentUser;
    @Mock
    private RabbitMqProducer rabbitMqProducer;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("encoded")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void login_ShouldReturnTokens_WhenValidCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.getExpiration(any())).thenReturn(LocalDateTime.now().plusDays(7));

        TokenResponse response = authService.login(new LoginRequest("test@example.com", "password"));
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_ShouldThrow_WhenInvalidEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> authService.login(new LoginRequest("test@example.com", "password")));
    }

    @Test
    void login_ShouldThrow_WhenInvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(false);
        assertThrows(BadRequestException.class, () -> authService.login(new LoginRequest("test@example.com", "password")));
    }
}
