package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ManagerDto {
    private UUID id;
    private UserSummary user;
    private PropertySummary property;
    private LocalDateTime assignedAt;
}
