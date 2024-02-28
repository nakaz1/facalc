package org.example.ref_calc.dto.sap;

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
public class CalculationsSapDto {
    private List<SapInterestExpenseDto> interestExpenses;
    private BigDecimal sumOfAmrs;
    private BigDecimal sumOfPerDay;
    private BigDecimal sumOfPerMonth;
    private BigDecimal afpp;
}
