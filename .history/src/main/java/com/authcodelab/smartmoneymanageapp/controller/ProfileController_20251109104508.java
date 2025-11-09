package com.authcodelab.smartmoneymanageapp.controller;

import com.authcodelab.smartmoneymanageapp.dto.AuthDTO;
import com.authcodelab.smartmoneymanageapp.dto.ProfileDTO;
import com.authcodelab.smartmoneymanageapp.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Registration successful! Please check your email to activate your account.",
                "user", registeredProfile));
    }

    @GetMapping("/activate")
    public ResponseEntity<Map<String, Object>> activateProfile(@RequestParam String token) {
        profileService.activateProfile(token);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Profile activated successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "Test Succesful";
    }

}
