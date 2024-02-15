package org.example.ref_calc.service;

import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.List;

public interface InterestExpenseCalculationService {
    List<InterestExpenseDto> calculateInterestExpenses(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities);
}
