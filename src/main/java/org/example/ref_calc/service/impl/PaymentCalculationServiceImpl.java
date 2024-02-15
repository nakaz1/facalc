package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.PaymentCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentCalculationServiceImpl implements PaymentCalculationService {
    @Override
    public List<PaymentDto> calculatePayments(LocalDate startDate, LocalDate endDate, BigDecimal interestRate, Double amount, LocalDate paymentDate) {
        log.info("Attempt to calculate AFPP");
        List<PaymentDto> payments = new ArrayList<>();
        payments.add(new PaymentDto(startDate, BigDecimal.ZERO));

        log.info("Begin date: " + startDate);
        log.info("Start date: " + endDate);

        try {
            // Расчет платежа с начала периода до первого платежа
//            double initialPayment = amount / Math.pow(1 + interestRate, (double) ChronoUnit.DAYS.between(startDate, paymentDate) / paymentDate.lengthOfYear());
            double pow = ChronoUnit.DAYS.between(startDate, paymentDate) / (double) paymentDate.lengthOfYear();
            double initialPayment = amount / Math.pow(1 + interestRate.doubleValue(), pow);
            PaymentDto initial = new PaymentDto(paymentDate, BigDecimal.valueOf(initialPayment));
            payments.add(initial);


            // Проходим через каждый месяц и выполняем расчеты
            LocalDate currentDate = paymentDate.plusMonths(1); // Начинаем с первого месяца после первого платежа
            while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {

                // Расчет платежа на текущую дату
                double powForMonth = (double) ChronoUnit.DAYS.between(startDate, currentDate) / paymentDate.lengthOfYear();
                double paymentAmount = amount / Math.pow(1 + interestRate.doubleValue(), powForMonth);
                PaymentDto payment = new PaymentDto(currentDate, BigDecimal.valueOf(paymentAmount));
                payments.add(payment);

                // Переход к следующему месяцу
                currentDate = currentDate.plusMonths(1);
            }

            // Расчет платежа на последний день периода
            double finalPayment = amount / paymentDate.lengthOfMonth();
            PaymentDto finalPaymentObj = new PaymentDto(endDate, BigDecimal.valueOf(finalPayment));
            payments.add(finalPaymentObj);
            BigDecimal totalPayment = payments.stream().map(PaymentDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var roundValue = (double) Math.round(totalPayment.doubleValue() * 100.0) / 100;
            log.info("Total amount of AFPP: {}", roundValue);

        } catch (ArithmeticException e) {
            log.error("Error in calculating AFPP: {}", e.getMessage());
        }

        return payments;
    }
}
