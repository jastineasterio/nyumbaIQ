package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaseServiceTest {
    @Mock
    private LeaseRepository leaseRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private ManagerAssignmentRepository managerAssignmentRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private LeaseService leaseService;

    @Test
    void createLease_ShouldCreate() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Owner")
                .lastName("Test")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").email("john@test.com").role(Role.TENANT).build())
                .fullName("John Doe")
                .status(TenantStatus.ACTIVE)
                .build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .status(PropertyStatus.ACTIVE)
                .build();

        Unit unit = Unit.builder()
                .id(UUID.randomUUID())
                .unitNumber("101")
                .status(UnitStatus.VACANT)
                .build();

        when(currentUser.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(leaseRepository.existsByUnitIdAndStatus(any(), any())).thenReturn(false);
        when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(any())).thenReturn(Optional.of(property));
        when(unitRepository.findById(any())).thenReturn(Optional.of(unit));
        when(leaseRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var request = new CreateLeaseRequest(tenant.getId(), unit.getId(), property.getId(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now().plusMonths(12), new BigDecimal("1000"), new BigDecimal("2000"),
                BillingFrequency.MONTHLY, 30, null);

        var result = leaseService.createLease(request);
        assertNotNull(result);
    }

    @Test
    void getLease_ShouldReturnLease() {
        UUID leaseId = UUID.randomUUID();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .status(PropertyStatus.ACTIVE)
                .build();
        Building building = Building.builder()
                .id(UUID.randomUUID())
                .name("Building A")
                .code("BLDG-1")
                .property(property)
                .status(BuildingStatus.ACTIVE)
                .build();
        Floor floor = Floor.builder()
                .id(UUID.randomUUID())
                .name("Ground Floor")
                .floorNumber(1)
                .building(building)
                .status(FloorStatus.ACTIVE)
                .build();
        Unit unit = Unit.builder()
                .id(UUID.randomUUID())
                .unitNumber("101")
                .floor(floor)
                .building(building)
                .property(property)
                .status(UnitStatus.OCCUPIED)
                .build();
        Lease lease = Lease.builder()
                .id(leaseId)
                .leaseNumber("LEASE-123")
                .status(LeaseStatus.DRAFT)
                .tenant(Tenant.builder().id(UUID.randomUUID()).user(User.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").email("john@test.com").role(Role.TENANT).build()).build())
                .unit(unit)
                .property(property)
                .building(building)
                .floor(floor)
                .build();

        when(leaseRepository.findById(leaseId)).thenReturn(Optional.of(lease));

        var result = leaseService.getLease(leaseId);
        assertNotNull(result);
    }

    @Test
    void getLease_ShouldThrow_WhenNotFound() {
        when(leaseRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> leaseService.getLease(UUID.randomUUID()));
    }
}
