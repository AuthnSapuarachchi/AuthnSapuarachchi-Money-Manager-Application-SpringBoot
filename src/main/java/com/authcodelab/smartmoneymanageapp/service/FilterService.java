package com.authcodelab.smartmoneymanageapp.service;

import com.authcodelab.smartmoneymanageapp.dto.ExpenseDTO;
import com.authcodelab.smartmoneymanageapp.dto.FilterDTO;
import com.authcodelab.smartmoneymanageapp.dto.IncomeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public List<?> filterTransactions(FilterDTO filterDTO) {
        // Validate transaction type
        validateTransactionType(filterDTO.getType());

        // Prepare filter parameters with defaults
        LocalDate startDate = filterDTO.getStartDate() != null
                ? filterDTO.getStartDate()
                : LocalDate.of(1900, 1, 1);

        LocalDate endDate = filterDTO.getEndDate() != null
                ? filterDTO.getEndDate()
                : LocalDate.of(2100, 12, 31);

        String keyword = filterDTO.getKeyword() != null
                ? filterDTO.getKeyword().trim()
                : "";

        String sortField = determineSortField(filterDTO.getSortField());
        Sort sort = createSort(filterDTO.getSortOrder(), sortField);

        log.info(
                "Filtering transactions - Type: {}, StartDate: {}, EndDate: {}, Keyword: {}, SortField: {}, SortOrder: {}",
                filterDTO.getType(), startDate, endDate, keyword, sortField, filterDTO.getSortOrder());

        // Route to appropriate service based on type
        if ("income".equalsIgnoreCase(filterDTO.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            log.info("Found {} income transactions", incomes.size());
            return incomes;
        } else {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            log.info("Found {} expense transactions", expenses.size());
            return expenses;
        }
    }

    private void validateTransactionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        String normalizedType = type.trim().toLowerCase();
        if (!"income".equals(normalizedType) && !"expense".equals(normalizedType)) {
            throw new IllegalArgumentException(
                    "Invalid transaction type: '" + type + "'. Must be 'income' or 'expense'");
        }
    }

    private String determineSortField(String sortField) {
        if (sortField == null || sortField.trim().isEmpty()) {
            return "date";
        }

        String normalizedField = sortField.trim().toLowerCase();

        // Validate against allowed sort fields
        switch (normalizedField) {
            case "date":
            case "amount":
            case "name":
                return normalizedField;
            default:
                log.warn("Invalid sort field '{}', defaulting to 'date'", sortField);
                return "date";
        }
    }

    private Sort createSort(String sortOrder, String sortField) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortField);
    }

    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date. Start: " + startDate + ", End: " + endDate);
        }
    }
}
