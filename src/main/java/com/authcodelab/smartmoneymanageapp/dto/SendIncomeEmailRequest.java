package com.authcodelab.smartmoneymanageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendIncomeEmailRequest {
    private String recipient;
    private List<IncomeEmailDTO> incomes;
}

