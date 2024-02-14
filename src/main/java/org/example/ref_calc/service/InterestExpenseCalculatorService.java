package org.example.ref_calc.service;

import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;

import java.util.List;

public interface InterestExpenseCalculatorService {
    List<InterestExpenseDto> calculateInterestExpenses(List<PaymentDto> payments, double interestRate, List<LiabilityDto> liabilities);
}
