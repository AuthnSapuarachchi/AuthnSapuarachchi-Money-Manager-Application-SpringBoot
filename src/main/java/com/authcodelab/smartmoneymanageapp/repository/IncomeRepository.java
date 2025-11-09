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

    // Find all expenses by profile id ordered by date descending
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Find top 5 most recent expenses by profile id
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    //select expenses by profile id and date range with keyword search and sorting
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    //select expenses by profile id and date range
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}
