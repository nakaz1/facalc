package org.example.ref_calc.service.excel;

import org.example.ref_calc.dto.excel.PaymentDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentCalculationService {
    List<PaymentDto> calculatePayments(LocalDate startDate, LocalDate endDate, BigDecimal interestRate, Double amount, LocalDate paymentDate);
}
