package com.authcodelab.smartmoneymanageapp.controller;

import com.authcodelab.smartmoneymanageapp.dto.SendIncomeEmailRequest;
import com.authcodelab.smartmoneymanageapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    @Autowired
    private final EmailService emailService;

    @PostMapping("/income")
    public ResponseEntity<?> sendIncomeReport(@RequestBody SendIncomeEmailRequest request) {
        try {
            emailService.sendIncomeReport(request.getRecipient(), request.getIncomes());
            return ResponseEntity.ok().body("Income report sent successfully to " + request.getRecipient());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}

