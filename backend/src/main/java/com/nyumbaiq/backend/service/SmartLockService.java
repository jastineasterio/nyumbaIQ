package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.exception.*;
import com.nyumbaiq.backend.smartlock.SmartLockProvider;
import com.nyumbaiq.backend.smartlock.SmartLockProviderFactory;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class SmartLockService {
    private final SmartLockRepository smartLockRepository;
    private final AccessCredentialRepository accessCredentialRepository;
    private final AccessEventRepository accessEventRepository;
    private final PropertyRepository propertyRepository;
    private final BuildingRepository buildingRepository;
    private final UnitRepository unitRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SmartLockProviderFactory providerFactory;
    private final CurrentUser currentUser;

    public SmartLockService(SmartLockRepository smartLockRepository,
                            AccessCredentialRepository accessCredentialRepository,
                            AccessEventRepository accessEventRepository,
                            PropertyRepository propertyRepository, BuildingRepository buildingRepository,
                            UnitRepository unitRepository, TenantRepository tenantRepository,
                            UserRepository userRepository, SmartLockProviderFactory providerFactory,
                            CurrentUser currentUser) {
        this.smartLockRepository = smartLockRepository;
        this.accessCredentialRepository = accessCredentialRepository;
        this.accessEventRepository = accessEventRepository;
        this.propertyRepository = propertyRepository;
        this.buildingRepository = buildingRepository;
        this.unitRepository = unitRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.providerFactory = providerFactory;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<SmartLockDto> getAllSmartLocks(Pageable pageable) {
        return smartLockRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public SmartLockDto getSmartLock(UUID id) {
        SmartLock lock = smartLockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        return toDto(lock);
    }

    public SmartLockDto createSmartLock(CreateSmartLockRequest request) {
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new NotFoundException("Property not found"));
        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new NotFoundException("Building not found"));
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new NotFoundException("Unit not found"));
        if (smartLockRepository.existsByLockCode(request.lockCode())) {
            throw new ConflictException("Lock code already exists");
        }
        SmartLock lock = SmartLock.builder()
                .lockCode(request.lockCode())
                .name(request.name())
                .property(property)
                .building(building)
                .unit(unit)
                .provider(request.provider() != null ? request.provider() : "mock")
                .status(LockStatus.ACTIVE)
                .lastStatusUpdate(LocalDateTime.now())
                .build();
        smartLockRepository.save(lock);
        return toDto(lock);
    }

    public SmartLockDto updateSmartLock(UUID id, UpdateSmartLockRequest request) {
        SmartLock lock = smartLockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        if (request.name() != null) lock.setName(request.name());
        if (request.provider() != null) lock.setProvider(request.provider());
        smartLockRepository.save(lock);
        return toDto(lock);
    }

    public SmartLockDto lock(UUID id) {
        SmartLock lock = smartLockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        SmartLockProvider provider = providerFactory.getProvider(lock.getProvider());
        provider.lock(lock.getProviderLockId());
        lock.setStatus(LockStatus.ACTIVE);
        lock.setLastStatusUpdate(LocalDateTime.now());
        smartLockRepository.save(lock);
        recordAccessEvent(lock, null, null, AccessAction.LOCK, AccessResult.SUCCESS, "API", null);
        return toDto(lock);
    }

    public SmartLockDto unlock(UUID id) {
        SmartLock lock = smartLockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        SmartLockProvider provider = providerFactory.getProvider(lock.getProvider());
        provider.unlock(lock.getProviderLockId());
        lock.setLastStatusUpdate(LocalDateTime.now());
        smartLockRepository.save(lock);
        recordAccessEvent(lock, null, null, AccessAction.UNLOCK, AccessResult.SUCCESS, "API", null);
        return toDto(lock);
    }

    public AccessCredentialDto createCredential(UUID lockId, CreateAccessCredentialRequest request) {
        SmartLock lock = smartLockRepository.findById(lockId)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        SmartLockProvider provider = providerFactory.getProvider(lock.getProvider());
        String credentialCode = request.credentialCode() != null ? request.credentialCode() :
                "CRED-" + System.currentTimeMillis();
        if (accessCredentialRepository.existsByCredentialCode(credentialCode)) {
            throw new ConflictException("Credential code already exists");
        }
        String providerReference = provider.createAccessCredential(lock.getProviderLockId(), tenant.getId().toString(), request.credentialType().name());
        AccessCredential credential = AccessCredential.builder()
                .credentialCode(credentialCode)
                .tenant(tenant)
                .lock(lock)
                .property(lock.getProperty())
                .unit(lock.getUnit())
                .credentialType(request.credentialType())
                .status(CredentialStatus.ACTIVE)
                .expiresAt(request.expiresAt())
                .createdBy(User.builder().id(currentUser.getUserId()).build())
                .build();
        accessCredentialRepository.save(credential);
        recordAccessEvent(lock, tenant.getId(), credential.getId(), AccessAction.CREDENTIAL_CREATED, AccessResult.SUCCESS, "API", providerReference);
        return toCredentialDto(credential);
    }

    public AccessCredentialDto revokeCredential(UUID lockId, UUID credentialId) {
        SmartLock lock = smartLockRepository.findById(lockId)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        AccessCredential credential = accessCredentialRepository.findById(credentialId)
                .orElseThrow(() -> new NotFoundException("Credential not found"));
        if (!credential.getLock().getId().equals(lockId)) {
            throw new BadRequestException("Credential does not belong to this lock");
        }
        SmartLockProvider provider = providerFactory.getProvider(lock.getProvider());
        provider.revokeAccessCredential(lock.getProviderLockId(), credential.getCredentialCode());
        credential.setStatus(CredentialStatus.REVOKED);
        credential.setRevokedAt(LocalDateTime.now());
        accessCredentialRepository.save(credential);
        recordAccessEvent(lock, credential.getTenant().getId(), credential.getId(), AccessAction.CREDENTIAL_REVOKED, AccessResult.SUCCESS, "API", null);
        return toCredentialDto(credential);
    }

    @Transactional(readOnly = true)
    public Page<AccessEventDto> getAccessEvents(UUID lockId, Pageable pageable) {
        smartLockRepository.findById(lockId)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        return accessEventRepository.findByLockId(lockId, pageable).map(this::toEventDto);
    }

    @Transactional(readOnly = true)
    public Page<AccessCredentialDto> getCredentials(UUID lockId, Pageable pageable) {
        smartLockRepository.findById(lockId)
                .orElseThrow(() -> new NotFoundException("Smart lock not found"));
        return accessCredentialRepository.findByLockId(lockId, pageable).map(this::toCredentialDto);
    }

    private void recordAccessEvent(SmartLock lock, UUID tenantId, UUID credentialId, AccessAction action, AccessResult result, String method, String providerReference) {
        AccessEvent event = AccessEvent.builder()
                .lock(lock)
                .tenant(tenantId != null ? Tenant.builder().id(tenantId).build() : null)
                .credential(credentialId != null ? AccessCredential.builder().id(credentialId).build() : null)
                .property(lock.getProperty())
                .unit(lock.getUnit())
                .action(action)
                .result(result)
                .method(method)
                .providerReference(providerReference)
                .timestamp(LocalDateTime.now())
                .build();
        accessEventRepository.save(event);
    }

    private SmartLockDto toDto(SmartLock lock) {
        Property property = lock.getProperty();
        Building building = lock.getBuilding();
        Unit unit = lock.getUnit();
        return new SmartLockDto(
                lock.getId(),
                lock.getLockCode(),
                lock.getName(),
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                building != null ? building.getId() : null,
                building != null ? building.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                lock.getProvider(),
                lock.getProviderLockId(),
                lock.getStatus(),
                lock.getLastStatusUpdate(),
                lock.getCreatedAt(),
                lock.getUpdatedAt()
        );
    }

    private AccessCredentialDto toCredentialDto(AccessCredential credential) {
        Tenant tenant = credential.getTenant();
        SmartLock lock = credential.getLock();
        Property property = credential.getProperty();
        Unit unit = credential.getUnit();
        User createdBy = credential.getCreatedBy();
        return new AccessCredentialDto(
                credential.getId(),
                credential.getCredentialCode(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getFullName() : null,
                lock != null ? lock.getId() : null,
                lock != null ? lock.getName() : null,
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                credential.getCredentialType(),
                credential.getStatus(),
                credential.getExpiresAt(),
                credential.getRevokedAt(),
                createdBy != null ? new UserSummary(createdBy.getId(), createdBy.getFirstName(), createdBy.getLastName(), createdBy.getEmail(), createdBy.getRole(), createdBy.getStatus()) : null,
                credential.getCreatedAt()
        );
    }

    private AccessEventDto toEventDto(AccessEvent event) {
        SmartLock lock = event.getLock();
        Tenant tenant = event.getTenant();
        AccessCredential credential = event.getCredential();
        Property property = event.getProperty();
        Unit unit = event.getUnit();
        return new AccessEventDto(
                event.getId(),
                lock != null ? lock.getId() : null,
                lock != null ? lock.getName() : null,
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getFullName() : null,
                credential != null ? credential.getId() : null,
                credential != null ? credential.getCredentialCode() : null,
                property != null ? property.getId() : null,
                property != null ? property.getName() : null,
                unit != null ? unit.getId() : null,
                unit != null ? unit.getUnitNumber() : null,
                event.getAction(),
                event.getResult(),
                event.getMethod(),
                event.getProviderReference(),
                event.getDeviceInfo(),
                event.getTimestamp(),
                event.getCreatedAt()
        );
    }
}
