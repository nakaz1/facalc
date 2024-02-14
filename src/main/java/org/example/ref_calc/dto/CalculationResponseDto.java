package org.example.ref_calc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalculationResponseDto {
    private double liability;
    private List<PaymentDto> payments;
    private List<InterestExpenseDto> interestExpenses;
//    private List<LiabilityDto> liabilities;
}
