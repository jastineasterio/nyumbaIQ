package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.PropertyStatus;
import com.nyumbaiq.backend.domain.repository.PropertyRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreatePropertyRequest;
import com.nyumbaiq.backend.dto.PropertyDto;
import com.nyumbaiq.backend.dto.PropertySummary;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.dto.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, CurrentUser currentUser) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public Page<PropertyDto> getAllProperties(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        return propertyRepository.findByOwnerId(userId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PropertyDto getProperty(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));
        return toDto(property);
    }

    public PropertyDto createProperty(CreatePropertyRequest request) {
        UUID ownerId = currentUser.getUserId();
        String code = generatePropertyCode();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        Property property = Property.builder()
                .name(request.name())
                .propertyCode(code)
                .description(request.description())
                .address(request.address())
                .city(request.city())
                .region(request.region())
                .country(request.country() != null ? request.country() : "Tanzania")
                .status(PropertyStatus.ACTIVE)
                .owner(owner)
                .build();
        propertyRepository.save(property);
        return toDto(property);
    }

    public PropertyDto updateProperty(UUID id, CreatePropertyRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));

        if (request.name() != null) property.setName(request.name());
        if (request.description() != null) property.setDescription(request.description());
        if (request.address() != null) property.setAddress(request.address());
        if (request.city() != null) property.setCity(request.city());
        if (request.region() != null) property.setRegion(request.region());
        if (request.country() != null) property.setCountry(request.country());

        propertyRepository.save(property);
        return toDto(property);
    }

    public void deleteProperty(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));
        propertyRepository.delete(property);
    }

    private String generatePropertyCode() {
        return "PROP-" + System.currentTimeMillis();
    }

    private PropertyDto toDto(Property property) {
        User owner = property.getOwner();
        return new PropertyDto(
                property.getId(),
                property.getName(),
                property.getPropertyCode(),
                property.getDescription(),
                property.getAddress(),
                property.getCity(),
                property.getRegion(),
                property.getCountry(),
                property.getStatus(),
                new UserSummary(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getEmail(), owner.getRole(), owner.getStatus()),
                property.getCreatedAt()
        );
    }
}
