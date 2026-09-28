package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.RefreshToken;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.RefreshTokenRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.ChangePasswordRequest;
import com.nyumbaiq.backend.dto.LoginRequest;
import com.nyumbaiq.backend.dto.RefreshTokenRequest;
import com.nyumbaiq.backend.dto.TokenResponse;
import com.nyumbaiq.backend.exception.BadRequestException;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.exception.UnauthorizedException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.security.JwtService;
import com.nyumbaiq.backend.event.AuditEventMessage;
import com.nyumbaiq.backend.service.RabbitMqProducer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final RabbitMqProducer rabbitMqProducer;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService, PasswordEncoder passwordEncoder, CurrentUser currentUser,
                       RabbitMqProducer rabbitMqProducer) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
        this.rabbitMqProducer = rabbitMqProducer;
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            rabbitMqProducer.sendAuditEvent(AuditEventMessage.builder()
                    .action("LOGIN_FAILED")
                    .entityType("USER")
                    .entityId(user.getId().toString())
                    .result(com.nyumbaiq.backend.domain.enums.AuditResult.FAILURE)
                    .ipAddress(currentUser.getIpAddress())
                    .userAgent(currentUser.getUserAgent())
                    .build());
            throw new BadRequestException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(jwtService.getExpiration(refreshToken))
                .build();
        refreshTokenRepository.save(tokenEntity);

        rabbitMqProducer.sendAuditEvent(AuditEventMessage.builder()
                .action("LOGIN")
                .entityType("USER")
                .entityId(user.getId().toString())
                .result(com.nyumbaiq.backend.domain.enums.AuditResult.SUCCESS)
                .ipAddress(currentUser.getIpAddress())
                .userAgent(currentUser.getUserAgent())
                .build());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshToken)
                .expiresAt(jwtService.getExpiration(newRefreshToken))
                .build();
        refreshTokenRepository.save(newToken);

        return new TokenResponse(accessToken, newRefreshToken);
    }

    public void logout() {
        UUID userId = currentUser.getUserId();
        refreshTokenRepository.findByUserIdAndTokenAndRevokedFalse(userId, null)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = currentUser.getUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
