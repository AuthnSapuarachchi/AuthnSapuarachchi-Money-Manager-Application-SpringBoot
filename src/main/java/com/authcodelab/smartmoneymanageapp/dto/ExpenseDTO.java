package com.authcodelab.smartmoneymanageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;
    private String name;
    private String icon;
    private String categoryName; // Kept for backward compatibility
    private Long categoryId; // Kept for backward compatibility
    private CategoryInfo category; // Added for frontend compatibility
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested class for category information
     * Matches frontend expectation: transaction.category?.name
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String icon;
    }
}
