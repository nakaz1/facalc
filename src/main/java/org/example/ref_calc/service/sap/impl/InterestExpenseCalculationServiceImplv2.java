package org.example.ref_calc.service.sap.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.sap.AmrDto;
import org.example.ref_calc.dto.sap.CalculationsSapDto;
import org.example.ref_calc.dto.sap.SapAfppDto;
import org.example.ref_calc.dto.sap.SapInterestExpenseDto;
import org.example.ref_calc.dto.sap.SapLiabilityDto;
import org.example.ref_calc.service.sap.InterestExpenseCalculationServiceV2;
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
    public CalculationsSapDto calculateInterestExpensesSap(List<SapAfppDto> payments, BigDecimal interestRate, Double amount, LocalDate beginDate, LocalDate endDate, BigDecimal beginValue) {
        log.info("Attempt to calculate interest expense");
        List<SapInterestExpenseDto> interestExpenses = new ArrayList<>();
        List<AmrDto> amortizations = new ArrayList<>();
        BigDecimal sumLiabilities = beginValue; //Сумма актива (АФПП)
        List<SapLiabilityDto> liabilityResult = new ArrayList<>();

        for (SapAfppDto payment : payments) {
            var daysOfMonth = payment.getDate().lengthOfMonth() - 1;
            var firstDayInMonth = payment.getDate().lengthOfMonth() - daysOfMonth;
            var multiplyPercent = interestRate.multiply(BigDecimal.valueOf(100));

            // Если дата платежа равна дате окончания дог отношений, платеж рассчитывается за 1 день
            if (payment.getDate().equals(endDate)) {
                amount = amount / payment.getDate().lengthOfMonth();
                amount = Math.round(amount * 100.0) / 100.0;
            }

            // Сумма процентов за 1-е число месяца
            BigDecimal sumOfInterestDay = calculatePerDay(sumLiabilities, interestRate, firstDayInMonth);

            interestExpenses.add(new SapInterestExpenseDto(payment.getDate().withDayOfMonth(1), sumOfInterestDay, firstDayInMonth, BigDecimal.valueOf(0), amount));

            // Расчет амортизации и процентов с начала срока исполнения
            if (payment.getDate().equals(beginDate)) {
                var amr = calculateAmr(amount, sumOfInterestDay);

                amortizations.add(new AmrDto(amr, payment.getDate()));

                sumLiabilities = sumLiabilities.subtract(payment.getAmount()
                        .subtract(sumLiabilities.multiply(multiplyPercent)
                                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                                .divide(BigDecimal.valueOf(DAYS_OF_YEAR), 10, RoundingMode.HALF_UP)));

                liabilityResult.add(new SapLiabilityDto(payment.getDate(), sumLiabilities));

                // Сумма процентов за первый месяц
                BigDecimal sumOfInterestMonth = calculatePerMonth(sumLiabilities, interestRate, daysOfMonth);

                interestExpenses.add(new SapInterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1),
                        sumOfInterestMonth,
                        daysOfMonth,
                        amr,
                        amount));

                // Последующие расчеты амортизации и процентов на основе процентов за прошлые месяца
            } else {
                var amr = calculateNewDataFromPreviousMonth(interestExpenses, amount, sumOfInterestDay);

                amortizations.add(new AmrDto(amr, payment.getDate()));

                //остаточная стоимость - амортизация
                sumLiabilities = sumLiabilities.subtract(amr);

                liabilityResult.add(new SapLiabilityDto(payment.getDate(), sumLiabilities));

                BigDecimal sumOfInterestMonth = calculatePerMonth(sumLiabilities, interestRate, daysOfMonth);

                interestExpenses.add(new SapInterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1),
                        sumOfInterestMonth,
                        daysOfMonth,
                        amr,
                        amount));
            }

        }


        //Общая сумма амортизации за весь срок исполнения
        BigDecimal sumAmrs = sumOfMonthAmr(amortizations);
        //Общая сумма процентов за 1 день всего срока исполнения
        BigDecimal sumPerDay = sumOfPerDay(interestExpenses);
        //Сумма процентов со 2 числа по конец месяца
        BigDecimal sumPerMonth = sumOfPerMonth(interestExpenses);

        var sumLia = liabilityResult.stream()
                .reduce((first, second) -> second)
                .map(SapLiabilityDto::getLiability)
                .orElseThrow();

        beginValue = sumLia.setScale(10, RoundingMode.HALF_UP);

        interestExpenses.forEach(dto -> {
            dto.setInterestExpense(dto.getInterestExpense().setScale(2, RoundingMode.HALF_UP));
            dto.setAmr(dto.getAmr().setScale(2, RoundingMode.HALF_UP));
        });

        log.info("Calculation of interest expenses was successful");

        return new CalculationsSapDto(interestExpenses, sumAmrs, sumPerDay, sumPerMonth, beginValue);
    }

    @Override
    public BigDecimal methodHord(CalculationsSapDto calculationsConstantX1, CalculationsSapDto calculationsConstantX2, BigDecimal x1, BigDecimal x2) {
        var sumLiabilityX1 = calculationsConstantX1.getAfpp();
        var sumLiabilityX2 = calculationsConstantX2.getAfpp();
        //Расчет остаточной стоимости по методу хорд x3 = x2 – y2 * (x2 – x1)/(y2 – y1)
        return x2.subtract(sumLiabilityX2.multiply(x2.subtract(x1)).divide(sumLiabilityX2.subtract(sumLiabilityX1), 2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateNewDataFromPreviousMonth(List<SapInterestExpenseDto> interestExpenses, Double amount, BigDecimal sumOfInterestDay) {
        BigDecimal amr = BigDecimal.ZERO;
        for (SapInterestExpenseDto expense : interestExpenses) {
            if (expense.getDays() > 1) {
                amr = BigDecimal.valueOf(amount).subtract(expense.getInterestExpense()).subtract(sumOfInterestDay);
            }
        }
        return amr;
    }

    private BigDecimal calculateAmr(Double amount, BigDecimal sumOfInterestDay) {
        return BigDecimal.valueOf(amount).subtract(sumOfInterestDay);
    }

    private BigDecimal sumOfPerDay(List<SapInterestExpenseDto> interestExpenses) {
        return interestExpenses.stream()
                .filter(x -> x.getDays() == 1)
                .map(SapInterestExpenseDto::getInterestExpense)
                .map(x -> x.setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumOfPerMonth(List<SapInterestExpenseDto> interestExpenses) {
        return interestExpenses.stream()
                .filter(x -> x.getDays() > 1)
                .map(SapInterestExpenseDto::getInterestExpense)
                .map(x -> x.setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumOfMonthAmr(List<AmrDto> amortizations) {
        return amortizations.stream()
                .map(AmrDto::getSumAmr)
                .map(x -> x.setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePerMonth(BigDecimal sumLiabilities, BigDecimal interestRate, int daysOfMonth) {
        return sumLiabilities
                .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                .multiply(BigDecimal.valueOf(daysOfMonth))
                .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculatePerDay(BigDecimal sumLiabilities, BigDecimal interestRate, int firstDayInMonth) {
        return sumLiabilities
                .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                .multiply(BigDecimal.valueOf(firstDayInMonth))
                .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }


}