package com.authcodelab.smartmoneymanageapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for filtering transactions
 * Used by the FilterController to filter income and expense transactions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterDTO {

    /**
     * Type of transaction to filter
     * Must be either 'income' or 'expense' (case-insensitive)
     */
    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "(?i)^(income|expense)$", message = "Type must be 'income' or 'expense'")
    private String type;

    /**
     * Start date for filtering (optional)
     * If not provided, defaults to 1900-01-01
     */
    private LocalDate startDate;

    /**
     * End date for filtering (optional)
     * If not provided, defaults to 2100-12-31
     */
    private LocalDate endDate;

    /**
     * Keyword to search in transaction name, category, or description (optional)
     * Case-insensitive partial match
     */
    private String keyword;

    /**
     * Field to sort by: 'date', 'amount', or 'name' (optional)
     * Defaults to 'date' if not provided or invalid
     */
    @Pattern(regexp = "(?i)^(date|amount|name)$", message = "Sort field must be 'date', 'amount', or 'name'")
    private String sortField;

    /**
     * Sort order: 'asc' or 'desc' (optional)
     * Defaults to 'desc' if not provided
     */
    @Pattern(regexp = "(?i)^(asc|desc)$", message = "Sort order must be 'asc' or 'desc'")
    private String sortOrder;
}
