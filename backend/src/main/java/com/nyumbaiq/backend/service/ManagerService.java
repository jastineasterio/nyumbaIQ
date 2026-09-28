package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.ManagerAssignment;
import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.ManagerAssignmentRepository;
import com.nyumbaiq.backend.domain.repository.PropertyRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreateManagerRequest;
import com.nyumbaiq.backend.dto.ManagerDto;
import com.nyumbaiq.backend.dto.PropertySummary;
import com.nyumbaiq.backend.dto.UserSummary;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class ManagerService {
    private final UserRepository userRepository;
    private final ManagerAssignmentRepository assignmentRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(UserRepository userRepository, ManagerAssignmentRepository assignmentRepository,
                          PropertyRepository propertyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<ManagerDto> getAllManagers(Pageable pageable) {
        return userRepository.findByRole(Role.MANAGER, pageable)
                .map(user -> {
                    ManagerAssignment assignment = assignmentRepository.findByManagerId(user.getId()).stream().findFirst().orElse(null);
                    Property property = assignment != null ? assignment.getProperty() : null;
                    return toDto(user, property, assignment != null ? assignment.getAssignedAt() : null);
                });
    }

    @Transactional(readOnly = true)
    public ManagerDto getManager(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if (user.getRole() != Role.MANAGER) {
            throw new NotFoundException("User is not a manager");
        }
        ManagerAssignment assignment = assignmentRepository.findByManagerId(id).stream().findFirst().orElse(null);
        Property property = assignment != null ? assignment.getProperty() : null;
        return toDto(user, property, assignment != null ? assignment.getAssignedAt() : null);
    }

    public ManagerDto createManager(CreateManagerRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.MANAGER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        return toDto(user, null, null);
    }

    public ManagerDto updateManager(UUID id, CreateManagerRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if (user.getRole() != Role.MANAGER) {
            throw new NotFoundException("User is not a manager");
        }

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.middleName() != null) user.setMiddleName(request.middleName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.username() != null) user.setUsername(request.username());

        userRepository.save(user);
        ManagerAssignment assignment = assignmentRepository.findByManagerId(id).stream().findFirst().orElse(null);
        Property property = assignment != null ? assignment.getProperty() : null;
        return toDto(user, property, assignment != null ? assignment.getAssignedAt() : null);
    }

    public void deleteManager(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if (user.getRole() != Role.MANAGER) {
            throw new NotFoundException("User is not a manager");
        }
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    public ManagerDto assignProperty(UUID managerId, UUID propertyId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if (manager.getRole() != Role.MANAGER) {
            throw new NotFoundException("User is not a manager");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found"));

        if (assignmentRepository.existsByManagerIdAndPropertyId(managerId, propertyId)) {
            throw new ConflictException("Manager already assigned to this property");
        }

        UUID assignedBy = new com.nyumbaiq.backend.security.CurrentUser().getUserId();
        ManagerAssignment assignment = ManagerAssignment.builder()
                .manager(manager)
                .property(property)
                .assignedAt(LocalDateTime.now())
                .assignedBy(userRepository.findById(assignedBy).orElseThrow())
                .build();
        assignmentRepository.save(assignment);
        return toDto(manager, property, assignment.getAssignedAt());
    }

    public void removeAssignment(UUID managerId, UUID assignmentId) {
        ManagerAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        if (!assignment.getManager().getId().equals(managerId)) {
            throw new NotFoundException("Assignment not found for this manager");
        }
        assignmentRepository.delete(assignment);
    }

    private ManagerDto toDto(User user, Property property, LocalDateTime assignedAt) {
        return new ManagerDto(
                user.getId(),
                new UserSummary(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.getStatus()),
                property != null ? new PropertySummary(property.getId(), property.getName(), property.getPropertyCode()) : null,
                assignedAt
        );
    }
}
