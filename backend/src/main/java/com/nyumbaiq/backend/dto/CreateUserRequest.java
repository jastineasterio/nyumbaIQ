package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        String username,
        @NotBlank @Size(min = 8) String password,
        Role role,
        UserStatus status
) {}
