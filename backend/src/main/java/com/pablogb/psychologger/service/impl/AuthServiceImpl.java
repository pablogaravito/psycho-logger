package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.LoginRequestDto;
import com.pablogb.psychologger.dto.request.RegisterRequestDto;
import com.pablogb.psychologger.dto.response.AuthResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.OrgSettings;
import com.pablogb.psychologger.model.entity.Organization;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.entity.UserSettings;
import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.repository.OrganizationRepository;
import com.pablogb.psychologger.repository.UserRepository;
import com.pablogb.psychologger.repository.UserSettingsRepository;
import com.pablogb.psychologger.security.JwtService;
import com.pablogb.psychologger.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserSettingsRepository userSettingsRepository;
    private final OrgSettingsRepository orgSettingsRepository;

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

//    @Override
//    @Transactional
//    public AuthResponseDto register(RegisterRequestDto request) {
//        Organization org = Organization.builder()
//                .name(request.getOrganizationName())
//                .build();
//        organizationRepository.save(org);
//
//        User user = User.builder()
//                .organization(org)
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .passwordHash(passwordEncoder.encode(request.getPassword()))
//                .isTherapist(true)
//                .isAdmin(true)
//                .isActive(true)
//                .build();
//        userRepository.save(user);
//
//        String token = jwtService.generateToken(user);
//        return toAuthResponse(user, token);
//    }


    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        Organization org = Organization.builder()
                .name(request.getOrganizationName())
                .build();
        organizationRepository.save(org);;

        User user = User.builder()
                .organization(org)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isTherapist(true)
                .isAdmin(true)
                .isActive(true)
                .build();
        userRepository.save(user);

        // seed user settings
        seedUserSettings(user);

        // seed org settings
        OrgSettings orgSettings = OrgSettings.builder()
                .organization(org)
                .defaultCurrency("USD")
                .preferredLanguage("en")
                .build();
        orgSettingsRepository.save(orgSettings);

        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

    private void seedUserSettings(User user) {
        UserSettings settings = UserSettings.builder()
                .user(user)
                .defaultSessionDuration(50)
                .showInactiveBirthdays(false)
                .build();
        userSettingsRepository.save(settings);
    }

    private AuthResponseDto toAuthResponse(User user, String token) {
        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isAdmin(user.getIsAdmin())
                .isTherapist(user.getIsTherapist())
                .orgId(user.getOrganization().getId())
                .build();
    }
}
