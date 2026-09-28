package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.TenantStatus;
import com.nyumbaiq.backend.domain.repository.TenantRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreateTenantRequest;
import com.nyumbaiq.backend.dto.TenantDto;
import com.nyumbaiq.backend.dto.TenantSummary;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TenantService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public TenantService(TenantRepository tenantRepository, UserRepository userRepository, CurrentUser currentUser) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<TenantDto> getAllTenants(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getRole() == Role.TENANT) {
            return tenantRepository.findByUserId(userId, pageable).map(this::toDto);
        }
        return tenantRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TenantDto getTenant(UUID id) {
        UUID userId = currentUser.getUserId();
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getRole() == Role.TENANT && !userId.equals(tenant.getUser() != null ? tenant.getUser().getId() : null)) {
            throw new com.nyumbaiq.backend.exception.AccessDeniedException("You are not allowed to access this tenant");
        }
        return toDto(tenant);
    }

    public TenantDto createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone already exists");
        }

        Tenant tenant = Tenant.builder()
                .fullName(request.fullName())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .emergencyContact(request.emergencyContact())
                .emergencyPhone(request.emergencyPhone())
                .status(request.status() != null ? request.status() : TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(tenant);
        return toDto(tenant);
    }

    public TenantDto updateTenant(UUID id, CreateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (request.fullName() != null) tenant.setFullName(request.fullName());
        if (request.phone() != null) tenant.setPhone(request.phone());
        if (request.email() != null) tenant.setEmail(request.email());
        if (request.address() != null) tenant.setAddress(request.address());
        if (request.emergencyContact() != null) tenant.setEmergencyContact(request.emergencyContact());
        if (request.emergencyPhone() != null) tenant.setEmergencyPhone(request.emergencyPhone());
        if (request.status() != null) tenant.setStatus(request.status());

        tenantRepository.save(tenant);
        return toDto(tenant);
    }

    public void deleteTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setStatus(TenantStatus.ARCHIVED);
        tenantRepository.save(tenant);
    }

    private TenantDto toDto(Tenant tenant) {
        User user = tenant.getUser();
        return new TenantDto(
                tenant.getId(),
                user != null ? new com.nyumbaiq.backend.dto.UserSummary(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.getStatus()) : null,
                tenant.getFullName(),
                tenant.getPhone(),
                tenant.getEmail(),
                tenant.getAddress(),
                tenant.getDateOfBirth(),
                tenant.getProfilePhotoPath(),
                tenant.getEmergencyContact(),
                tenant.getEmergencyPhone(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }
}
