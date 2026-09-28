package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
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
public class LeaseService {
    private final LeaseRepository leaseRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ManagerAssignmentRepository managerAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final CurrentUser currentUser;

    public LeaseService(LeaseRepository leaseRepository, UnitRepository unitRepository,
                        UserRepository userRepository, PropertyRepository propertyRepository,
                        ManagerAssignmentRepository managerAssignmentRepository, TenantRepository tenantRepository,
                        CurrentUser currentUser) {
        this.leaseRepository = leaseRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.managerAssignmentRepository = managerAssignmentRepository;
        this.tenantRepository = tenantRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<LeaseDto> getAllLeases(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.TENANT) {
            return leaseRepository.findByTenantId(userId, pageable).map(this::toDto);
        }

        if (user.getRole() == Role.MANAGER) {
            return leaseRepository.findAll(pageable).map(this::toDto);
        }

        return leaseRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public LeaseDto getLease(UUID id) {
        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease not found"));
        return toDto(lease);
    }

    @Transactional(readOnly = true)
    public LeaseDto getLeaseByNumber(String leaseNumber) {
        Lease lease = leaseRepository.findByLeaseNumber(leaseNumber)
                .orElseThrow(() -> new NotFoundException("Lease not found"));
        return toDto(lease);
    }

    public LeaseDto createLease(CreateLeaseRequest request) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only owner or manager can create leases");
        }

        if (user.getRole() == Role.MANAGER) {
            boolean assigned = managerAssignmentRepository.existsByManagerIdAndPropertyId(userId, request.propertyId());
            if (!assigned) {
                throw new AccessDeniedException("Manager not assigned to this property");
            }
        }

        if (leaseRepository.existsByUnitIdAndStatus(request.unitId(), LeaseStatus.ACTIVE)) {
            throw new ConflictException("Unit already has an active lease");
        }

        String leaseNumber = "LEASE-" + System.currentTimeMillis();
        Tenant tenant = tenantRepository.findById(request.tenantId()).orElseThrow(() -> new NotFoundException("Tenant not found"));
        Property property = propertyRepository.findById(request.propertyId()).orElseThrow(() -> new NotFoundException("Property not found"));
        Building building = null;
        Floor floor = null;
        Unit unit = unitRepository.findById(request.unitId()).orElseThrow(() -> new NotFoundException("Unit not found"));

        Lease lease = Lease.builder()
                .leaseNumber(leaseNumber)
                .tenant(tenant)
                .unit(unit)
                .property(property)
                .building(building)
                .floor(floor)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .rentAmount(request.rentAmount())
                .securityDeposit(request.securityDeposit())
                .billingFrequency(request.billingFrequency())
                .noticePeriodDays(request.noticePeriodDays())
                .leaseTerms(request.leaseTerms())
                .status(LeaseStatus.DRAFT)
                .createdBy(user)
                .build();

        leaseRepository.save(lease);
        return toDto(lease);
    }

    public LeaseDto updateLease(UUID id, UpdateLeaseRequest request) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only owner or manager can update leases");
        }

        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease not found"));

        if (request.startDate() != null) lease.setStartDate(request.startDate());
        if (request.endDate() != null) lease.setEndDate(request.endDate());
        if (request.rentAmount() != null) lease.setRentAmount(request.rentAmount());
        if (request.securityDeposit() != null) lease.setSecurityDeposit(request.securityDeposit());
        if (request.noticePeriodDays() != null) lease.setNoticePeriodDays(request.noticePeriodDays());
        if (request.leaseTerms() != null) lease.setLeaseTerms(request.leaseTerms());
        if (request.status() != null) {
            lease.setStatus(request.status());
            updateUnitStatus(lease);
        }

        leaseRepository.save(lease);
        return toDto(lease);
    }

    public LeaseDto renewLease(UUID id) {
        Lease previousLease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease not found"));

        if (previousLease.getStatus() != LeaseStatus.ACTIVE && previousLease.getStatus() != LeaseStatus.EXPIRING) {
            throw new BadRequestException("Cannot renew lease with status: " + previousLease.getStatus());
        }

        String leaseNumber = "LEASE-" + System.currentTimeMillis();
        Lease newLease = Lease.builder()
                .leaseNumber(leaseNumber)
                .tenant(previousLease.getTenant())
                .unit(previousLease.getUnit())
                .property(previousLease.getProperty())
                .building(previousLease.getBuilding())
                .floor(previousLease.getFloor())
                .startDate(previousLease.getEndDate().plusDays(1))
                .endDate(previousLease.getEndDate().plusMonths(12))
                .rentAmount(previousLease.getRentAmount())
                .securityDeposit(previousLease.getSecurityDeposit())
                .billingFrequency(previousLease.getBillingFrequency())
                .noticePeriodDays(previousLease.getNoticePeriodDays())
                .leaseTerms(previousLease.getLeaseTerms())
                .status(LeaseStatus.DRAFT)
                .previousLease(previousLease)
                .createdBy(previousLease.getCreatedBy())
                .build();

        previousLease.setStatus(LeaseStatus.RENEWED);
        leaseRepository.save(previousLease);
        leaseRepository.save(newLease);

        return toDto(newLease);
    }

    public LeaseDto approveLease(UUID id) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only owner or manager can approve leases");
        }

        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease not found"));

        if (lease.getStatus() != LeaseStatus.PENDING_APPROVAL) {
            throw new BadRequestException("Lease is not pending approval");
        }

        lease.setStatus(LeaseStatus.ACTIVE);
        lease.setApprovedBy(user);
        lease.setApprovedAt(LocalDateTime.now());

        updateUnitStatus(lease);
        leaseRepository.save(lease);
        return toDto(lease);
    }

    public LeaseDto terminateLease(UUID id) {
        UUID userId = currentUser.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != Role.OWNER && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only owner or manager can terminate leases");
        }

        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lease not found"));

        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setEndDate(LocalDateTime.now());

        updateUnitStatus(lease);
        leaseRepository.save(lease);
        return toDto(lease);
    }

    private void updateUnitStatus(Lease lease) {
        Unit unit = lease.getUnit();
        if (lease.getStatus() == LeaseStatus.ACTIVE) {
            unit.setStatus(UnitStatus.OCCUPIED);
        } else if (lease.getStatus() == LeaseStatus.TERMINATED || lease.getStatus() == LeaseStatus.EXPIRED) {
            unit.setStatus(UnitStatus.VACANT);
        }
        unitRepository.save(unit);
    }

    private LeaseDto toDto(Lease lease) {
        User tenantUser = lease.getTenant() != null ? lease.getTenant().getUser() : null;
        User createdBy = lease.getCreatedBy();
        User approvedBy = lease.getApprovedBy();
        User previousTenant = lease.getPreviousLease() != null ? lease.getPreviousLease().getTenant().getUser() : null;

        return new LeaseDto(
                lease.getId(),
                lease.getLeaseNumber(),
                tenantUser != null ? new UserSummary(tenantUser.getId(), tenantUser.getFirstName(), tenantUser.getLastName(), tenantUser.getEmail(), tenantUser.getRole(), tenantUser.getStatus()) : null,
                new PropertySummary(lease.getProperty().getId(), lease.getProperty().getName(), lease.getProperty().getPropertyCode()),
                new BuildingSummary(lease.getBuilding() != null ? lease.getBuilding().getId() : null, lease.getBuilding() != null ? lease.getBuilding().getName() : null, lease.getBuilding() != null ? lease.getBuilding().getCode() : null),
                new FloorSummary(lease.getFloor() != null ? lease.getFloor().getId() : null, lease.getFloor() != null ? lease.getFloor().getName() : null, null),
                new UnitSummary(lease.getUnit().getId(), lease.getUnit().getUnitNumber()),
                previousTenant != null ? new UserSummary(previousTenant.getId(), previousTenant.getFirstName(), previousTenant.getLastName(), previousTenant.getEmail(), previousTenant.getRole(), previousTenant.getStatus()) : null,
                lease.getStartDate(),
                lease.getEndDate(),
                lease.getRentAmount(),
                lease.getSecurityDeposit(),
                lease.getBillingFrequency(),
                lease.getNoticePeriodDays(),
                lease.getLeaseTerms(),
                lease.getStatus(),
                createdBy != null ? new UserSummary(createdBy.getId(), createdBy.getFirstName(), createdBy.getLastName(), createdBy.getEmail(), createdBy.getRole(), createdBy.getStatus()) : null,
                approvedBy != null ? new UserSummary(approvedBy.getId(), approvedBy.getFirstName(), approvedBy.getLastName(), approvedBy.getEmail(), approvedBy.getRole(), approvedBy.getStatus()) : null,
                lease.getApprovedAt(),
                lease.getCreatedAt()
        );
    }
}
