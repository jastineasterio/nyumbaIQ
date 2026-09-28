package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSummary {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private UserStatus status;
}
