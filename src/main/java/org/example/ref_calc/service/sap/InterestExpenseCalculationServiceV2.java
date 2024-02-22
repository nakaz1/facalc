package org.example.ref_calc.service.sap;

import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.sap.CalculationsSapDto;
import org.example.ref_calc.dto.sap.SapAfppDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterestExpenseCalculationServiceV2 {
    CalculationsSapDto calculateInterestExpensesSap(List<SapAfppDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities, Double amount, LocalDate beginDate, LocalDate endDate);
}
