package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.smartlock.SmartLockProviderFactory;
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
class SmartLockServiceTest {
    @Mock
    private SmartLockRepository smartLockRepository;
    @Mock
    private AccessCredentialRepository accessCredentialRepository;
    @Mock
    private AccessEventRepository accessEventRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SmartLockProviderFactory providerFactory;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private SmartLockService smartLockService;

    @Test
    void getAllSmartLocks_ShouldReturnPage() {
        SmartLock lock = SmartLock.builder()
                .id(UUID.randomUUID())
                .lockCode("LOCK-123")
                .name("Front Door")
                .status(LockStatus.ACTIVE)
                .build();
        Page<SmartLock> page = new PageImpl<>(List.of(lock));
        when(smartLockRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = smartLockService.getAllSmartLocks(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getSmartLock_ShouldReturnLock() {
        SmartLock lock = SmartLock.builder()
                .id(UUID.randomUUID())
                .lockCode("LOCK-123")
                .name("Front Door")
                .status(LockStatus.ACTIVE)
                .build();
        when(smartLockRepository.findById(lock.getId())).thenReturn(Optional.of(lock));
        SmartLockDto dto = smartLockService.getSmartLock(lock.getId());
        assertEquals(lock.getLockCode(), dto.getLockCode());
    }
}
