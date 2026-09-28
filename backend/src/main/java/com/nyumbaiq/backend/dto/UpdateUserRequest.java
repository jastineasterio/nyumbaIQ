package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String firstName,
        String middleName,
        String lastName,
        String phone,
        String username,
        Role role,
        UserStatus status
) {}
