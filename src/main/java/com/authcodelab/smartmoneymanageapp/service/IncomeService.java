package com.authcodelab.smartmoneymanageapp.service;

import com.authcodelab.smartmoneymanageapp.dto.ExpenseDTO;
import com.authcodelab.smartmoneymanageapp.dto.IncomeDTO;
import com.authcodelab.smartmoneymanageapp.entity.CategoryEntity;
import com.authcodelab.smartmoneymanageapp.entity.ExpenseEntity;
import com.authcodelab.smartmoneymanageapp.entity.IncomeEntity;
import com.authcodelab.smartmoneymanageapp.entity.ProfileEntity;
import com.authcodelab.smartmoneymanageapp.repository.CategoryRepository;
import com.authcodelab.smartmoneymanageapp.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // add expense
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newExpense = toEntity(incomeDTO, profile, category);
        newExpense = incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // Retrieves all incomes for current month/based on the date and end date
    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    // delete expenses
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!existingIncome.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(existingIncome);
    }

    // Get latest 5 incomes fr current user
    public List<IncomeDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // Get total expenses for current user
    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    // filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list;

        // Use enhanced search if keyword is provided, otherwise get all in date range
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = incomeRepository.searchIncomesByKeyword(
                    profile.getId(),
                    startDate,
                    endDate,
                    keyword.trim(),
                    sort);
        } else {
            // If no keyword, get all incomes in date range and apply sorting
            list = incomeRepository.findByProfileIdAndDateBetween(
                    profile.getId(),
                    startDate,
                    endDate);
            // Note: Manual sorting would be needed here if using this branch
            // For simplicity, we can use the keyword search with empty string
            list = incomeRepository.searchIncomesByKeyword(
                    profile.getId(),
                    startDate,
                    endDate,
                    "",
                    sort);
        }

        return list.stream().map(this::toDTO).toList();
    }

    // helper method to convert DTO to Entity
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .amount(dto.getAmount())
                .icon(dto.getIcon())
                .name(dto.getName())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity) {
        IncomeDTO.CategoryInfo categoryInfo = null;
        if (entity.getCategory() != null) {
            categoryInfo = IncomeDTO.CategoryInfo.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .icon(entity.getCategory().getIcon())
                    .build();
        }

        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .category(categoryInfo)
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
