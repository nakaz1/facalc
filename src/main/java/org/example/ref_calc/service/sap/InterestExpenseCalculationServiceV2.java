package org.example.ref_calc.service.sap;

import org.example.ref_calc.dto.sap.CalculationsSapDto;
import org.example.ref_calc.dto.sap.SapAfppDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterestExpenseCalculationServiceV2 {
    CalculationsSapDto calculateInterestExpensesSap(List<SapAfppDto> payments, BigDecimal interestRate, Double amount, LocalDate beginDate, LocalDate endDate, BigDecimal beginValue);

    BigDecimal methodHord(CalculationsSapDto calculationsConstantX1, CalculationsSapDto calculationsConstantX2, BigDecimal x1, BigDecimal x2);
}
