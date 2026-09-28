package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.TenantStatus;
import com.nyumbaiq.backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TenantDto {
    private UUID id;
    private UserSummary user;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private String profilePhotoUrl;
    private String emergencyContact;
    private String emergencyPhone;
    private TenantStatus status;
    private LocalDateTime createdAt;
}
