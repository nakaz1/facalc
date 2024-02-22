package org.example.ref_calc.service.sap.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.sap.SapAfppDto;
import org.example.ref_calc.service.sap.PaymentCalculationServiceV2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentCalculationServiceImplV2 implements PaymentCalculationServiceV2 {
    @Override
    public List<SapAfppDto> calculatePayments(LocalDate startDate, LocalDate endDate, BigDecimal interestRate, Double amount, LocalDate paymentDate) {
        log.info("Attempt to calculate AFPP");
        List<SapAfppDto> payments = new ArrayList<>();

        log.info("Begin date: " + startDate);
        log.info("End date: " + endDate);
        try {
            double pow = ChronoUnit.DAYS.between(startDate, paymentDate) / (double) paymentDate.lengthOfYear();
            double initialPayment = amount / Math.pow(1 + interestRate.doubleValue(), pow);
            SapAfppDto initial = new SapAfppDto(paymentDate, BigDecimal.valueOf(initialPayment));
            payments.add(initial);

            // Проходим через каждый месяц и выполняем расчеты
            LocalDate currentDate = paymentDate.plusMonths(1); // Начинаем с первого месяца после первого платежа
            while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
                if (currentDate.equals(endDate)) {
                    amount = amount / endDate.lengthOfMonth();
                    double powForMonth = (double) ChronoUnit.DAYS.between(startDate, currentDate) / paymentDate.lengthOfYear();
                    double finalPayment = amount / Math.pow(1 + interestRate.doubleValue(), powForMonth);
                    payments.add(new SapAfppDto(currentDate, BigDecimal.valueOf(finalPayment)));
                    break;
                }

                // Расчет платежа на текущую дату
                double powForMonth = (double) ChronoUnit.DAYS.between(startDate, currentDate) / paymentDate.lengthOfYear();
                double paymentAmount = amount / Math.pow(1 + interestRate.doubleValue(), powForMonth);
                payments.add(new SapAfppDto(currentDate, BigDecimal.valueOf(paymentAmount)));

                // Переход к следующему месяцу
                currentDate = currentDate.plusMonths(1);
            }

            BigDecimal totalPayment = payments.stream().map(SapAfppDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var roundValue = (double) Math.round(totalPayment.doubleValue() * 100.0) / 100;
            log.info("Total amount of AFPP: {}", roundValue);

        } catch (ArithmeticException e) {
            log.error("Error in calculating AFPP: {}", e.getMessage());
        }

        return payments;
    }
}
