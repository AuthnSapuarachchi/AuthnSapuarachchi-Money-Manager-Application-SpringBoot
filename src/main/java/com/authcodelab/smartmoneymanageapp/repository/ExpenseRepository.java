package com.authcodelab.smartmoneymanageapp.repository;

import com.authcodelab.smartmoneymanageapp.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // Find all expenses by profile id ordered by date descending
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Find top 5 most recent expenses by profile id
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    //select expenses by profile id and date range with keyword search and sorting
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    //select expenses by profile id and date range
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select expenses by profile id and specific date
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

}
