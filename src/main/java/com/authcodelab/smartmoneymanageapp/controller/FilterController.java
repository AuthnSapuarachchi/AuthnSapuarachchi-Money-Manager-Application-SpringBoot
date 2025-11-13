package com.authcodelab.smartmoneymanageapp.controller;

import com.authcodelab.smartmoneymanageapp.dto.FilterDTO;
import com.authcodelab.smartmoneymanageapp.service.FilterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filters")
@Slf4j
public class FilterController {

    private final FilterService filterService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@Valid @RequestBody FilterDTO filterDTO) {
        try {
            log.info("Received filter request: {}", filterDTO);

            // Validate date range
            if (filterDTO.getStartDate() != null && filterDTO.getEndDate() != null) {
                filterService.validateDateRange(filterDTO.getStartDate(), filterDTO.getEndDate());
            }

            // Get filtered transactions
            List<?> results = filterService.filterTransactions(filterDTO);

            log.info("Successfully filtered {} transactions", results.size());
            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid filter request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Error filtering transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An error occurred while filtering transactions"));
        }
    }


    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", java.time.LocalDateTime.now());
        return error;
    }
}
