package org.example.ref_calc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculationDtoV2 {
    private List<InterestExpenseDto> interestExpenses;
    private BigDecimal sumOfAmrs;
    private BigDecimal sumOfPerDay;
    private BigDecimal sumOfPerMonth;
}
