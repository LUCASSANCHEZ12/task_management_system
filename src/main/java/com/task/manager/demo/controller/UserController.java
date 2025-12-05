package com.task.manager.demo.controller;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;
import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.service.profile.ProfileService;
import com.task.manager.demo.service.user.UserService;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final UserService service;
    private final ProfileService profileService;

    public UserController(UserService service, ProfileService profileService) {
        this.service = service;
        this.profileService = profileService;
    }

    @Operation(summary = "Get user by ID", description = "Requires ADMIN role")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get all users", description = "Requires ADMIN role")
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Update user", description = "Requires ADMIN role")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Requires ADMIN role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Create or update user profile", description = "Requires USER or ADMIN role")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> updateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody ProfileUpdateDTO request
    ) {
        boolean isNewProfile = !profileService.existsByUserId(userId);
        ProfileDto profileDto = profileService.createOrUpdateProfile(userId, request);

        if (isNewProfile) {
            return ResponseEntity.status(HttpStatus.CREATED).body(profileDto);
        } else {
            return ResponseEntity.ok(profileDto);
        }
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile", description = "Requires USER or ADMIN role")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> getProfile(
            @PathVariable UUID userId
    ) {
        ProfileDto profileDto = profileService.findByUserId(userId);
        return ResponseEntity.ok(profileDto);
    }
}


