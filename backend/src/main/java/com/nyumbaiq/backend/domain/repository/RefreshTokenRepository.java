package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    Optional<RefreshToken> findByUserIdAndTokenAndRevokedFalse(UUID userId, String token);
}
