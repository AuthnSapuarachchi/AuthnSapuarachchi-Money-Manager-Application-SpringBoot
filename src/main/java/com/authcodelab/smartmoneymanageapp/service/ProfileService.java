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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Check if email already exists
        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email address is already registered: " + profileDTO.getEmail());
        }

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // --- HTML EMAIL DESIGN START ---
        String activationLink = activationURL + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Welcome to Smart Money! 🚀 Activate your account";

        String body = """
        <html>
          <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;">
            <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
              
              <h2 style="color: #333333; text-align: center;">Welcome to Smart Money!</h2>
              
              <p style="color: #555555; font-size: 16px;">Dear <strong>%s</strong>,</p>
              
              <p style="color: #555555; font-size: 16px;">
                Thank you for registering. We are excited to help you take control of your finances! 
                Please click the button below to verify your email address and activate your account.
              </p>
              
              <div style="text-align: center; margin: 30px 0;">
                <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block; font-weight: bold;">
                  Verify My Account
                </a>
              </div>
              
              <p style="color: #999999; font-size: 14px; text-align: center;">
                If the button above doesn't work, copy and paste this link into your browser:<br>
                <a href="%s" style="color: #4CAF50;">%s</a>
              </p>
              
              <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
              
              <p style="color: #aaaaaa; font-size: 12px; text-align: center;">
                Best regards,<br>
                <strong>Smart Money Manage App Team</strong>
              </p>
            </div>
          </body>
        </html>
        """.formatted(newProfile.getFullName(), activationLink, activationLink, activationLink);

        // Send the email (Make sure your service supports HTML!)
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
                    if (profile.getIsActive()) {
                        throw new InvalidActivationTokenException("Account is already activated");
                    }
                    profile.setIsActive(true);
                    profile.setActivationToken(null); // Clear the token after activation
                    profileRepository.save(profile);
                    return true;
                })
                .orElseThrow(() -> new InvalidActivationTokenException("Invalid activation token"));
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found for email: " + email));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .password(currentUser.getPassword())
                .email(currentUser.getEmail())
                .profilePictureUrl(currentUser.getProfilePictureUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        // Check if account exists and is active
        if (!isAccountActive(authDTO.getEmail())) {
            throw new UserAccountNotActivatedException(
                    "Account is not activated. Please check your email and activate your account before logging in.");
        }

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

        // Generate token and return response
        String token = jwtUtill.generateToken(authDTO.getEmail());
        return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail()),
                "tokenType", "Bearer");
    }
}
