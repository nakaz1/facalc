package org.example.ref_calc.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CalculationResponseDto {
    private BigDecimal afpp;
    private List<PaymentDto> payments;
    private List<InterestExpenseDto> interestExpenses;
    private CalculationDtoV2 calculations;
}
