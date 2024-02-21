package org.example.ref_calc.service;

import org.example.ref_calc.dto.CalculationDtoV2;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterestExpenseCalculationServiceV2 {


    CalculationDtoV2 calculateInterestExpenses(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities, Double amount, LocalDate beginDate, LocalDate endDate);
}
