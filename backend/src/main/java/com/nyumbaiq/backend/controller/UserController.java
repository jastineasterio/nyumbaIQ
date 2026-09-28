package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.CreateUserRequest;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.dto.UpdateUserRequest;
import com.nyumbaiq.backend.dto.UserDto;
import com.nyumbaiq.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        UUID userId = new com.nyumbaiq.backend.security.CurrentUser().getUserId();
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponse<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> page = userService.getAllUsers(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable UUID id, @RequestBody com.nyumbaiq.backend.domain.enums.UserStatus status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }
}
