package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.PropertyStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.PropertyRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreatePropertyRequest;
import com.nyumbaiq.backend.dto.PropertyDto;
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
class PropertyServiceTest {
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void getAllProperties_ShouldReturnPage() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .id(ownerId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .address("123 Test St")
                .city("Test City")
                .region("Test Region")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        when(currentUser.getUserId()).thenReturn(ownerId);
        Page<Property> page = new PageImpl<>(List.of(property));
        when(propertyRepository.findByOwnerId(any(), any())).thenReturn(page);
        var result = propertyService.getAllProperties(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getProperty_ShouldReturnProperty() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .id(ownerId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .address("123 Test St")
                .city("Test City")
                .region("Test Region")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        PropertyDto dto = propertyService.getProperty(property.getId());
        assertEquals(property.getName(), dto.getName());
    }

    @Test
    void getProperty_ShouldThrow_WhenNotFound() {
        when(propertyRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> propertyService.getProperty(UUID.randomUUID()));
    }

    @Test
    void createProperty_ShouldCreateProperty() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .id(ownerId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .address("123 Test St")
                .city("Test City")
                .region("Test Region")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        when(currentUser.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any())).thenReturn(property);
        var result = propertyService.createProperty(new CreatePropertyRequest("Test", "Desc", "Addr", "City", "Region", "Tanzania"));
        assertNotNull(result);
    }

    @Test
    void deleteProperty_ShouldDelete() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .id(ownerId)
                .firstName("Test")
                .lastName("Owner")
                .email("owner@test.com")
                .role(Role.OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .name("Test Property")
                .propertyCode("PROP-123")
                .address("123 Test St")
                .city("Test City")
                .region("Test Region")
                .owner(owner)
                .status(PropertyStatus.ACTIVE)
                .build();
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        propertyService.deleteProperty(property.getId());
        verify(propertyRepository, times(1)).delete(property);
    }
}
