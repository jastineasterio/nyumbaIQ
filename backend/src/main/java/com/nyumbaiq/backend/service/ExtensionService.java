package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.event.NotificationEventMessage;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class ExtensionService {
    private final RentalExtensionRepository extensionRepository;
    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    public ExtensionService(RentalExtensionRepository extensionRepository, LeaseRepository leaseRepository,
                            PropertyRepository propertyRepository, UnitRepository unitRepository,
                            TenantRepository tenantRepository, UserRepository userRepository,
                            NotificationService notificationService, CurrentUser currentUser) {
        this.extensionRepository = extensionRepository;
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.unitRepository = unitRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<RentalExtensionDto> getAllExtensions(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            Tenant tenant = tenantRepository.findByUserId(userId).orElse(null);
            if (tenant == null) {
                return Page.empty();
            }
            return extensionRepository.findByTenantId(tenant.getId(), pageable).map(this::toDto);
        }
        return extensionRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RentalExtensionDto getExtension(UUID id) {
        RentalExtension extension = extensionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Extension not found"));
        return toDto(extension);
    }

    public RentalExtensionDto createExtension(CreateRentalExtensionRequest requestDto) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Lease lease = leaseRepository.findById(requestDto.leaseId())
                .orElseThrow(() -> new NotFoundException("Lease not found"));
        if (user.getRole() == Role.TENANT && !lease.getTenant().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only request extensions for your own lease");
        }
        String extensionNumber = "EXT-" + System.currentTimeMillis();
        RentalExtension extension = RentalExtension.builder()
                .extensionNumber(extensionNumber)
                .tenant(lease.getTenant())
                .lease(lease)
                .property(lease.getProperty())
                .unit(lease.getUnit())
                .originalDueDate(requestDto.originalDueDate())
                .newDueDate(requestDto.newDueDate())
                .reason(requestDto.reason())
                .status(ExtensionStatus.PENDING)
                .build();
        extensionRepository.save(extension);
        notificationService.sendNotification(
                lease.getTenant().getUser() != null ? lease.getTenant().getUser().getId() : null,
                lease.getTenant().getId(),
                lease.getProperty().getId(),
                NotificationChannel.IN_APP,
                "EXTENSION_REQUESTED",
                "Rental Extension Requested",
                "A rental extension request " + extensionNumber + " has been submitted."
        );
        return toDto(extension);
    }

    public RentalExtensionDto approveExtension(UUID id, ReviewRentalExtensionRequest requestDto) {
        RentalExtension extension = extensionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Extension not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot approve extensions");
        }
        if (extension.getStatus() != ExtensionStatus.PENDING) {
            throw new BadRequestException("Extension is not pending");
        }
        extension.setStatus(ExtensionStatus.APPROVED);
        extension.setApprovedBy(User.builder().id(userId).build());
        extension.setApprovedAt(LocalDateTime.now());
        extensionRepository.save(extension);
        notificationService.sendNotification(
                extension.getTenant().getUser() != null ? extension.getTenant().getUser().getId() : null,
                extension.getTenant().getId(),
                extension.getProperty().getId(),
                NotificationChannel.IN_APP,
                "EXTENSION_APPROVED",
                "Rental Extension Approved",
                "Your rental extension request " + extension.getExtensionNumber() + " has been approved."
        );
        return toDto(extension);
    }

    public RentalExtensionDto rejectExtension(UUID id, ReviewRentalExtensionRequest requestDto) {
        RentalExtension extension = extensionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Extension not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            throw new AccessDeniedException("Tenants cannot reject extensions");
        }
        if (extension.getStatus() != ExtensionStatus.PENDING) {
            throw new BadRequestException("Extension is not pending");
        }
        extension.setStatus(ExtensionStatus.REJECTED);
        extension.setApprovedBy(User.builder().id(userId).build());
        extension.setApprovedAt(LocalDateTime.now());
        extensionRepository.save(extension);
        return toDto(extension);
    }

    public RentalExtensionDto cancelExtension(UUID id) {
        RentalExtension extension = extensionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Extension not found"));
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (extension.getStatus() != ExtensionStatus.PENDING) {
            throw new BadRequestException("Only pending extensions can be cancelled");
        }
        if (user.getRole() == Role.TENANT && !extension.getTenant().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only cancel your own extensions");
        }
        extension.setStatus(ExtensionStatus.CANCELLED);
        extensionRepository.save(extension);
        return toDto(extension);
    }

    private RentalExtensionDto toDto(RentalExtension extension) {
        User approvedBy = extension.getApprovedBy();
        return new RentalExtensionDto(
                extension.getId(),
                extension.getExtensionNumber(),
                extension.getTenant() != null ? extension.getTenant().getId() : null,
                extension.getTenant() != null ? extension.getTenant().getFullName() : null,
                extension.getLease() != null ? extension.getLease().getId() : null,
                extension.getLease() != null ? extension.getLease().getLeaseNumber() : null,
                extension.getProperty() != null ? extension.getProperty().getId() : null,
                extension.getProperty() != null ? extension.getProperty().getName() : null,
                extension.getUnit() != null ? extension.getUnit().getId() : null,
                extension.getUnit() != null ? extension.getUnit().getUnitNumber() : null,
                extension.getOriginalDueDate(),
                extension.getNewDueDate(),
                extension.getReason(),
                extension.getStatus(),
                approvedBy != null ? new UserSummary(approvedBy.getId(), approvedBy.getFirstName(), approvedBy.getLastName(), approvedBy.getEmail(), approvedBy.getRole(), approvedBy.getStatus()) : null,
                extension.getApprovedAt(),
                extension.getCreatedAt()
        );
    }
}
