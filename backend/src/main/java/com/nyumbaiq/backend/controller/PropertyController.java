package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.CreatePropertyRequest;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.dto.PropertyDto;
import com.nyumbaiq.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<PropertyDto>> getAllProperties(Pageable pageable) {
        Page<PropertyDto> page = propertyService.getAllProperties(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyDto> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.createProperty(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyDto> getProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyService.getProperty(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyDto> updateProperty(@PathVariable UUID id, @Valid @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.updateProperty(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
