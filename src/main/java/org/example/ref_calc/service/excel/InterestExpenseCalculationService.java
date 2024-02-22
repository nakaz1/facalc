package org.example.ref_calc.service.excel;

import org.example.ref_calc.dto.excel.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.excel.PaymentDto;

import java.math.BigDecimal;
import java.util.List;

public interface InterestExpenseCalculationService {
    List<InterestExpenseDto> calculateInterestExpensesExcel(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities);
}
