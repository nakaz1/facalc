package org.example.ref_calc.dto.excel;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ExcelCalculationResponseDto {
    private BigDecimal afpp;
    private List<PaymentDto> payments;
    private List<InterestExpenseDto> interestExpenses;
}
