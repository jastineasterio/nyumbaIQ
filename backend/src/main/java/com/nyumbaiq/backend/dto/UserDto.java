package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private Role role;
    private UserStatus status;
    private String profilePhotoUrl;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
