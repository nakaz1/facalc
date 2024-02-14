package org.example.ref_calc.service;

import org.example.ref_calc.dto.PaymentDto;

import java.time.LocalDate;
import java.util.List;

public interface PaymentCalculationService {
    List<PaymentDto> calculatePayments(LocalDate startDate, LocalDate endDate, double interestRate, double amount, LocalDate paymentDate);
}
