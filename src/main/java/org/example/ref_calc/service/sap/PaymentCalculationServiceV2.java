package org.example.ref_calc.service.sap;

import org.example.ref_calc.dto.sap.SapAfppDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentCalculationServiceV2 {
    List<SapAfppDto> calculatePayments(LocalDate startDate, LocalDate endDate, BigDecimal interestRate, Double amount, LocalDate paymentDate);
}
