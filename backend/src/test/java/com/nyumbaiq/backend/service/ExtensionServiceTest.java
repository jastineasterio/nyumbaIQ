package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtensionServiceTest {
    @Mock
    private RentalExtensionRepository extensionRepository;
    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ExtensionService extensionService;

    @Test
    void getAllExtensions_ShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        RentalExtension extension = RentalExtension.builder()
                .id(UUID.randomUUID())
                .extensionNumber("EXT-123")
                .status(ExtensionStatus.PENDING)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Page<RentalExtension> page = new PageImpl<>(List.of(extension));
        when(extensionRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = extensionService.getAllExtensions(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }
}
