package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.UserRequestDto;
import com.pablogb.psychologger.dto.response.UserResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.repository.UserRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

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

        return toResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateTherapist(Integer id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

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

        return toResponseDto(userRepository.save(user));
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
        userRepository.save(user);
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