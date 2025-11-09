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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    //add expense
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newExpense = toEntity(incomeDTO, profile, category);
        newExpense = incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    //Retrieves all incomes for current month/based on the date and end date
    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //delete expenses
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!existingIncome.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(existingIncome);
    }

    //helper method to convert DTO to Entity
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
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
