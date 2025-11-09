package com.authcodelab.smartmoneymanageapp.controller;

import com.authcodelab.smartmoneymanageapp.dto.AuthDTO;
import com.authcodelab.smartmoneymanageapp.dto.ProfileDTO;
import com.authcodelab.smartmoneymanageapp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.ok(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid activation token.");
        }
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
