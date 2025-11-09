package com.authcodelab.smartmoneymanageapp.service;

import com.authcodelab.smartmoneymanageapp.dto.AuthDTO;
import com.authcodelab.smartmoneymanageapp.dto.ProfileDTO;
import com.authcodelab.smartmoneymanageapp.entity.ProfileEntity;
import com.authcodelab.smartmoneymanageapp.exception.DuplicateEmailException;
import com.authcodelab.smartmoneymanageapp.exception.InvalidActivationTokenException;
import com.authcodelab.smartmoneymanageapp.exception.UserAccountNotActivatedException;
import com.authcodelab.smartmoneymanageapp.repository.ProfileRepository;
import com.authcodelab.smartmoneymanageapp.util.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtill jwtUtill;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Check if email already exists
        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email address is already registered: " + profileDTO.getEmail());
        }

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Send activation email
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Smart Money Manage App account";
        String body = "Dear " + newProfile.getFullName() + ",\n\n"
                + "Please activate your account by clicking the link below:\n"
                + activationLink + "\n\n"
                + "Best regards,\n"
                + "Smart Money Manage App Team";
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profilePictureUrl(profileDTO.getProfilePictureUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profilePictureUrl(profileEntity.getProfilePictureUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);

    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .profilePictureUrl(currentUser.getProfilePictureUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();

    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            String token = jwtUtill.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
