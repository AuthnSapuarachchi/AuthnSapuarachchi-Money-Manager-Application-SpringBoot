package com.authcodelab.smartmoneymanageapp.repository;

import com.authcodelab.smartmoneymanageapp.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // Find all incomes by profile id ordered by date descending
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Find top 5 most recent incomes by profile id
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // Calculate total income for a profile
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // Select incomes by profile id and date range with keyword search and sorting
    // (name only)
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort);

    /**
     * Enhanced search across name and category name
     * Searches for keyword in both income name and category name
     */
    @Query("SELECT i FROM IncomeEntity i " +
            "WHERE i.profile.id = :profileId " +
            "AND i.date BETWEEN :startDate AND :endDate " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(i.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<IncomeEntity> searchIncomesByKeyword(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword,
            Sort sort);

    // Select incomes by profile id and date range
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}
