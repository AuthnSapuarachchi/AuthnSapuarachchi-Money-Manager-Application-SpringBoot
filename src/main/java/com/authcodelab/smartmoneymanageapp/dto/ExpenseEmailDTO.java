package com.authcodelab.smartmoneymanageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEmailDTO {
    private LocalDate date;
    private String source;
    private BigDecimal amount;
    private String description;
}

