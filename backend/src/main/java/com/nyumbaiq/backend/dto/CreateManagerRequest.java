package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateManagerRequest(
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        String username,
        @NotBlank @Size(min = 8) String password
) {}
