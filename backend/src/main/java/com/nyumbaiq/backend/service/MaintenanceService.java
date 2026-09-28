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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class MaintenanceService {
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaintenanceCostRepository maintenanceCostRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ManagerAssignmentRepository managerAssignmentRepository;
    private final LeaseRepository leaseRepository;
    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    public MaintenanceService(MaintenanceRequestRepository maintenanceRequestRepository,
                              MaintenanceCostRepository maintenanceCostRepository,
                              PropertyRepository propertyRepository, UnitRepository unitRepository,
                              UserRepository userRepository, TenantRepository tenantRepository,
                              ManagerAssignmentRepository managerAssignmentRepository,
                              LeaseRepository leaseRepository,
                              NotificationService notificationService, CurrentUser currentUser) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.maintenanceCostRepository = maintenanceCostRepository;
        this.propertyRepository = propertyRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.managerAssignmentRepository = managerAssignmentRepository;
        this.leaseRepository = leaseRepository;
        this.notificationService = notificationService;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceRequestDto> getAllMaintenanceRequests(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() == Role.TENANT) {
            Tenant tenant = tenantRepository.findByUserId(userId).orElse(null);
            if (tenant == null) {
                return Page.empty();
            }
            return maintenanceRequestRepository.findByTenantId(tenant.getId(), pageable).map(this::toDto);
        }
        return maintenanceRequestRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public MaintenanceRequestDto getMaintenanceRequest(UUID id) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        return toDto(request);
    }

    public MaintenanceRequestDto createMaintenanceRequest(CreateMaintenanceRequestRequest requestDto) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Property property = propertyRepository.findById(requestDto.propertyId())
                .orElseThrow(() -> new NotFoundException("Property not found"));
        Unit unit = unitRepository.findById(requestDto.unitId())
                .orElseThrow(() -> new NotFoundException("Unit not found"));
        Tenant tenant;
        if (user.getRole() == Role.TENANT) {
            tenant = tenantRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Tenant profile not found"));
        } else {
            Lease activeLease = leaseRepository.findByUnitIdAndStatus(unit.getId(), LeaseStatus.ACTIVE).orElse(null);
            if (activeLease == null) {
                throw new NotFoundException("No active lease found for unit");
            }
            tenant = activeLease.getTenant();
        }
        String requestNumber = "MREQ-" + System.currentTimeMillis();
        MaintenanceRequest request = MaintenanceRequest.builder()
                .requestNumber(requestNumber)
                .tenant(tenant)
                .property(property)
                .unit(unit)
                .category(requestDto.category())
                .title(requestDto.title())
                .description(requestDto.description())
                .priority(MaintenancePriority.MEDIUM)
                .status(MaintenanceStatus.SUBMITTED)
                .photos(requestDto.photos())
                .build();
        maintenanceRequestRepository.save(request);
        notificationService.sendNotification(
                tenant.getUser() != null ? tenant.getUser().getId() : userId,
                tenant.getId(),
                property.getId(),
                NotificationChannel.IN_APP,
                "MAINTENANCE_CREATED",
                "Maintenance Request Created",
                "Your maintenance request " + requestNumber + " has been submitted."
        );
        return toDto(request);
    }

    public MaintenanceRequestDto updateMaintenanceRequest(UUID id, UpdateMaintenanceRequestRequest requestDto) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        if (requestDto.category() != null) request.setCategory(requestDto.category());
        if (requestDto.title() != null) request.setTitle(requestDto.title());
        if (requestDto.description() != null) request.setDescription(requestDto.description());
        if (requestDto.priority() != null) request.setPriority(requestDto.priority());
        if (requestDto.photos() != null) request.setPhotos(requestDto.photos());
        maintenanceRequestRepository.save(request);
        return toDto(request);
    }

    public MaintenanceRequestDto assignMaintenanceRequest(UUID id, UUID assigneeId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        request.setAssignedTo(assignee);
        request.setStatus(MaintenanceStatus.ASSIGNED);
        maintenanceRequestRepository.save(request);
        return toDto(request);
    }

    public MaintenanceRequestDto updateMaintenanceStatus(UUID id, MaintenanceStatus status) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        request.setStatus(status);
        maintenanceRequestRepository.save(request);
        return toDto(request);
    }

    public MaintenanceCostDto addMaintenanceCost(UUID requestId, CreateMaintenanceCostRequest requestDto) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        Property property = request.getProperty();
        BigDecimal totalCost = requestDto.labourCost()
                .add(requestDto.materialsCost())
                .add(requestDto.vendorCost());
        MaintenanceCost cost = MaintenanceCost.builder()
                .maintenanceRequest(request)
                .property(property)
                .unit(request.getUnit())
                .category(requestDto.category())
                .description(requestDto.description())
                .labourCost(requestDto.labourCost())
                .materialsCost(requestDto.materialsCost())
                .vendorCost(requestDto.vendorCost())
                .totalCost(totalCost)
                .vendor(requestDto.vendor())
                .notes(requestDto.notes())
                .build();
        maintenanceCostRepository.save(cost);
        return toCostDto(cost);
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceCostDto> getMaintenanceCosts(UUID requestId, Pageable pageable) {
        maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Maintenance request not found"));
        return maintenanceCostRepository.findByMaintenanceRequestId(requestId, pageable).map(this::toCostDto);
    }

    private MaintenanceRequestDto toDto(MaintenanceRequest request) {
        User assignedTo = request.getAssignedTo();
        Tenant tenant = request.getTenant();
        Property property = request.getProperty();
        Unit unit = request.getUnit();
        return new MaintenanceRequestDto(
                request.getId(),
                request.getRequestNumber(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getFullName() : null,
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                request.getCategory(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getStatus(),
                assignedTo != null ? assignedTo.getId() : null,
                assignedTo != null ? assignedTo.getFirstName() + " " + assignedTo.getLastName() : null,
                request.getPhotos(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private MaintenanceCostDto toCostDto(MaintenanceCost cost) {
        Property property = cost.getProperty();
        Unit unit = cost.getUnit();
        return new MaintenanceCostDto(
                cost.getId(),
                cost.getMaintenanceRequest() != null ? cost.getMaintenanceRequest().getId() : null,
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                cost.getCategory(),
                cost.getDescription(),
                cost.getLabourCost(),
                cost.getMaterialsCost(),
                cost.getVendorCost(),
                cost.getTotalCost(),
                cost.getVendor(),
                cost.getNotes(),
                cost.getCreatedAt()
        );
    }
}
