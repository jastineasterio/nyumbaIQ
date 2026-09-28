package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.CreateUserRequest;
import com.nyumbaiq.backend.dto.UpdateUserRequest;
import com.nyumbaiq.backend.dto.UserDto;
import com.nyumbaiq.backend.exception.BadRequestException;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.security.PasswordEncoderConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        UUID userId = new CurrentUser().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toDto(user);
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }
        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone already exists");
        }
        if (request.username() != null && userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .status(request.status() != null ? request.status() : UserStatus.PENDING_VERIFICATION)
                .build();
        userRepository.save(user);
        return toDto(user);
    }

    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.middleName() != null) user.setMiddleName(request.middleName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.username() != null) user.setUsername(request.username());
        if (request.role() != null) user.setRole(request.role());
        if (request.status() != null) user.setStatus(request.status());

        userRepository.save(user);
        return toDto(user);
    }

    public UserDto updateUserStatus(UUID id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                user.getRole(),
                user.getStatus(),
                user.getProfilePhotoPath(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
}
