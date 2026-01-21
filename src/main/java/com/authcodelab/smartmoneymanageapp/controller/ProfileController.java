package com.authcodelab.smartmoneymanageapp.controller;

import com.authcodelab.smartmoneymanageapp.dto.AuthDTO;
import com.authcodelab.smartmoneymanageapp.dto.ProfileDTO;
import com.authcodelab.smartmoneymanageapp.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Value("${app.frontend.url}") // Default to localhost if not set
    private String frontendUrl;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Registration successful! Please check your email to activate your account.",
                "user", registeredProfile));
    }

//    @GetMapping("/activate")
//    public ResponseEntity<Map<String, Object>> activateProfile(@RequestParam String token) {
//        profileService.activateProfile(token);
//        URI loginPageUri = URI.create(frontendUrl + "/login");
//        return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "message", "Profile activated successfully."));
//    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activateProfile(@RequestParam String token) {
        // 1. Activate the profile
        profileService.activateProfile(token);

        // 2. Define where to go (The Login Page
        URI loginPageUri = URI.create(frontendUrl + "/login");

        // 3. Send a "302 Found" Redirect response
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(loginPageUri)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthDTO authDTO) {
        Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getCurrentProfile() {
        ProfileDTO profile = profileService.getPublicProfile(null);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "user", profile));
    }

    @GetMapping("/test")
    public String test() {
        return "Test Successful";
    }

}
