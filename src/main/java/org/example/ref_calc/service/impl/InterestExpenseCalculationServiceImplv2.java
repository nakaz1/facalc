package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.AmrDto;
import org.example.ref_calc.dto.CalculationDtoV2;
import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.InterestExpenseCalculationServiceV2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InterestExpenseCalculationServiceImplv2 implements InterestExpenseCalculationServiceV2 {

    private static final int DAYS_OF_YEAR = 366;

    @Override
    public CalculationDtoV2 calculateInterestExpenses(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities, Double amount, LocalDate beginDate, LocalDate endDate) {
        log.info("Attempt to calculate interest expense");
        List<InterestExpenseDto> interestExpenses = new ArrayList<>();
        List<AmrDto> amortizations = new ArrayList<>();
        var sum = BigDecimal.ZERO;
        var count = 0;
        var sumLiabilities = liabilities.get(0).getLiability().setScale(2, RoundingMode.HALF_UP); //Сумма актива (АФПП)

        for (PaymentDto payment : payments) {
            var daysOfMonth = payment.getDate().lengthOfMonth() - 1;
            var firstDayInMonth = payment.getDate().lengthOfMonth() - daysOfMonth;

            if (payment.getDate().equals(endDate)) {
                amount = amount / payment.getDate().lengthOfMonth();
            }

            // Сумма процентов за 1-е число месяца
            BigDecimal sumOfInterestDay = sumLiabilities
                    .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                    .multiply(BigDecimal.valueOf(firstDayInMonth))
                    .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(1), sumOfInterestDay.setScale(2, RoundingMode.HALF_UP), firstDayInMonth, BigDecimal.valueOf(0), amount));

            if (payment.getDate().equals(beginDate)) {
                var amr1 = BigDecimal.valueOf(amount).subtract(sumOfInterestDay);
                amortizations.add(new AmrDto(amr1, payment.getDate()));
                sumLiabilities = sumLiabilities.subtract(amr1);
                // Сумма процентов за месяц
                BigDecimal sumOfInterestMonth = sumLiabilities
                        .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                        .multiply(BigDecimal.valueOf(daysOfMonth))
                        .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1),
                        sumOfInterestMonth.setScale(2, RoundingMode.HALF_UP),
                        daysOfMonth,
                        amr1.setScale(2, RoundingMode.HALF_UP),
                        amount));

            } else {
                List<InterestExpenseDto> tmp = interestExpenses.stream().filter(x -> x.getDays() > 1).toList();
                var amr1 = BigDecimal.valueOf(amount).subtract(tmp.get(count).getInterestExpense()).subtract(sumOfInterestDay);
                amortizations.add(new AmrDto(amr1, payment.getDate()));
                count++;
                sumLiabilities = sumLiabilities.subtract(amr1);
                // Сумма процентов за месяц
                BigDecimal sumOfInterestMonth = sumLiabilities
                        .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                        .multiply(BigDecimal.valueOf(daysOfMonth))
                        .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1),
                        sumOfInterestMonth.setScale(2, RoundingMode.HALF_UP),
                        daysOfMonth,
                        amr1.setScale(2, RoundingMode.HALF_UP),
                        amount));
            }
        }

            sum = amortizations.stream()
                    .map(AmrDto::getSumAmr)
                    .map(x -> x.setScale(2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var sumPerDay = interestExpenses.stream().filter(x -> x.getDays() == 1).map(InterestExpenseDto::getInterestExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
            var sumPerMonth = interestExpenses.stream().filter(x -> x.getDays() > 1).map(InterestExpenseDto::getInterestExpense).reduce(BigDecimal.ZERO, BigDecimal::add);

            log.info("Calculation of interest expenses was successful");

        return new CalculationDtoV2(interestExpenses, sum, sumPerDay, sumPerMonth);
    }


}