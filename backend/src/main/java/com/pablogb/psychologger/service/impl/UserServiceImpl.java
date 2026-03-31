package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.UserRequestDto;
import com.pablogb.psychologger.dto.response.UserResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.entity.UserSettings;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.repository.TherapistPatientAssignmentRepository;
import com.pablogb.psychologger.repository.UserRepository;
import com.pablogb.psychologger.repository.UserSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;
    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final AuditServiceImpl auditService;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllTherapists() {
        Integer orgId = securityUtils.getCurrentOrgId();
        return userRepository.findByOrganizationId(orgId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getTherapistById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        return toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto createTherapist(UserRequestDto request) {
        User currentUser = securityUtils.getCurrentUser();

        User user = User.builder()
                .organization(currentUser.getOrganization())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isTherapist(request.getIsTherapist() != null
                        ? request.getIsTherapist() : true)
                .isAdmin(request.getIsAdmin() != null
                        ? request.getIsAdmin() : false)
                .isActive(true)
                .build();
        User createdUser = userRepository.save(user);
        auditService.log(AuditAction.CREATE, "User", createdUser.getId(),
                "User created: " + createdUser.getEmail()
                        + " roles: therapist=" + createdUser.getIsTherapist()
                        + " admin=" + createdUser.getIsAdmin());
        // seed user settings
        seedUserSettings(createdUser);

        return toResponseDto(createdUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateTherapist(Integer id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        List<String> changes = new ArrayList<>();

        // Track changes before updating
        if (request.getIsTherapist() != null &&
                !request.getIsTherapist().equals(user.getIsTherapist())) {
            changes.add("isTherapist: " + user.getIsTherapist()
                    + " → " + request.getIsTherapist());
        }
        if (request.getIsAdmin() != null &&
                !request.getIsAdmin().equals(user.getIsAdmin())) {
            changes.add("isAdmin: " + user.getIsAdmin()
                    + " → " + request.getIsAdmin());
        }
        if (request.getIsActive() != null &&
                !request.getIsActive().equals(user.getIsActive())) {
            changes.add("isActive: " + user.getIsActive()
                    + " → " + request.getIsActive());
        }

        // if removing therapist role, close all active assignments
        if (request.getIsTherapist() != null
                && !request.getIsTherapist()
                && user.getIsTherapist()) {
            assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(user.getId())
                    .forEach(a -> {
                        a.setUnassignedAt(LocalDateTime.now());
                        assignmentRepository.save(a);
                    });
            changes.add("Closed all active assignments");
        }

        // Apply field updates
        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank())
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (request.getIsTherapist() != null)
            user.setIsTherapist(request.getIsTherapist());
        if (request.getIsAdmin() != null)
            user.setIsAdmin(request.getIsAdmin());
        if (request.getIsActive() != null)
            user.setIsActive(request.getIsActive());

        User savedUser = userRepository.save(user);

        // Log the changes
        String details = changes.isEmpty() ? "Profile info updated" : String.join(", ", changes);
        auditService.log(AuditAction.UPDATE, "User", id, details);

        return toResponseDto(savedUser);
    }

    @Override
    @Transactional
    public void deactivateTherapist(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        // prevent deactivating yourself
        if (user.getId().equals(securityUtils.getCurrentUserId())) {
            throw new RuntimeException("Cannot deactivate your own account");
        }

        user.setIsActive(false);
        User deactivatedUser = userRepository.save(user);
        auditService.log(AuditAction.DELETE, "User", deactivatedUser.getId(),
                "User deactivated: " + deactivatedUser.getEmail());
    }

    private void seedUserSettings(User user) {
        UserSettings settings = UserSettings.builder()
                .user(user)
                .defaultSessionDuration(50)
                .showInactiveBirthdays(false)
                .timeFormat("24h")
                .build();
        userSettingsRepository.save(settings);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isTherapist(user.getIsTherapist())
                .isAdmin(user.getIsAdmin())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}