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
class MaintenanceServiceTest {
    @Mock
    private MaintenanceRequestRepository maintenanceRequestRepository;
    @Mock
    private MaintenanceCostRepository maintenanceCostRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private ManagerAssignmentRepository managerAssignmentRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private MaintenanceService maintenanceService;

    @Test
    void getAllMaintenanceRequests_ShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.MANAGER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .owner(user)
                .status(PropertyStatus.ACTIVE)
                .build();
        MaintenanceRequest request = MaintenanceRequest.builder()
                .id(UUID.randomUUID())
                .requestNumber("MREQ-123")
                .property(property)
                .category("Plumbing")
                .title("Leaky faucet")
                .description("Desc")
                .priority(MaintenancePriority.MEDIUM)
                .status(MaintenanceStatus.SUBMITTED)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Page<MaintenanceRequest> page = new PageImpl<>(List.of(request));
        when(maintenanceRequestRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = maintenanceService.getAllMaintenanceRequests(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }
}
