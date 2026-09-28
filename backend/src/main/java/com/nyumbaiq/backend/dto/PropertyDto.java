package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PropertyDto {
    private UUID id;
    private String name;
    private String propertyCode;
    private String description;
    private String address;
    private String city;
    private String region;
    private String country;
    private PropertyStatus status;
    private UserSummary owner;
    private LocalDateTime createdAt;
}
