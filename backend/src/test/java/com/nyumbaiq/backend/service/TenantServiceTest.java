package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.enums.TenantStatus;
import com.nyumbaiq.backend.domain.repository.TenantRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreateTenantRequest;
import com.nyumbaiq.backend.dto.TenantDto;
import com.nyumbaiq.backend.exception.ConflictException;
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
class TenantServiceTest {
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private TenantService tenantService;

    private Tenant tenant;

    @Test
    void getAllTenants_ShouldReturnPage() {
        tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .fullName("Test Tenant")
                .phone("+255712345678")
                .email("tenant@test.com")
                .status(TenantStatus.ACTIVE)
                .build();
        Page<Tenant> page = new PageImpl<>(List.of(tenant));
        when(tenantRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = tenantService.getAllTenants(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getTenant_ShouldReturnTenant() {
        tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .fullName("Test Tenant")
                .phone("+255712345678")
                .email("tenant@test.com")
                .status(TenantStatus.ACTIVE)
                .build();
        when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        TenantDto dto = tenantService.getTenant(tenant.getId());
        assertEquals(tenant.getFullName(), dto.getFullName());
    }

    @Test
    void getTenant_ShouldThrow_WhenNotFound() {
        when(tenantRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tenantService.getTenant(UUID.randomUUID()));
    }

    @Test
    void createTenant_ShouldCreate() {
        tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .fullName("Test Tenant")
                .phone("+255712345678")
                .email("tenant@test.com")
                .status(TenantStatus.ACTIVE)
                .build();
        when(tenantRepository.existsByPhone("+255712345678")).thenReturn(false);
        when(tenantRepository.save(any())).thenReturn(tenant);
        var result = tenantService.createTenant(new CreateTenantRequest("Test", "+255712345678", "t@t.com", null, null, null, null, TenantStatus.ACTIVE));
        assertNotNull(result);
    }

    @Test
    void createTenant_ShouldThrow_WhenPhoneExists() {
        when(tenantRepository.existsByPhone("+255712345678")).thenReturn(true);
        assertThrows(ConflictException.class, () -> tenantService.createTenant(new CreateTenantRequest("Test", "+255712345678", "t@t.com", null, null, null, null, TenantStatus.ACTIVE)));
    }
}
