package org.example.ref_calc.service.sap.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.sap.AmrDto;
import org.example.ref_calc.dto.sap.CalculationsSapDto;
import org.example.ref_calc.dto.sap.SapAfppDto;
import org.example.ref_calc.dto.sap.SapInterestExpenseDto;
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
    public CalculationsSapDto calculateInterestExpensesSap(List<SapAfppDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities, Double amount, LocalDate beginDate, LocalDate endDate) {
        log.info("Attempt to calculate interest expense");
        List<SapInterestExpenseDto> interestExpenses = new ArrayList<>();
        List<AmrDto> amortizations = new ArrayList<>();
        var sumLiabilities = liabilities.get(0).getLiability().setScale(2, RoundingMode.HALF_UP); //Сумма актива (АФПП)
        BigDecimal sum;
        BigDecimal sumPerDay;
        BigDecimal sumPerMonth;

        for (SapAfppDto payment : payments) {
            var daysOfMonth = payment.getDate().lengthOfMonth() - 1;
            var firstDayInMonth = payment.getDate().lengthOfMonth() - daysOfMonth;

            // Если дата платежа равна дате окончания дог отношений, платеж рассчитывается за 1 день
            if (payment.getDate().equals(endDate)) {
                amount = amount / payment.getDate().lengthOfMonth();
            }

            // Сумма процентов за 1-е число месяца
            BigDecimal sumOfInterestDay = calculatePerDay(sumLiabilities, interestRate, firstDayInMonth);

            interestExpenses.add(new SapInterestExpenseDto(payment.getDate().withDayOfMonth(1), sumOfInterestDay.setScale(2, RoundingMode.HALF_UP), firstDayInMonth, BigDecimal.valueOf(0), amount));

            // Расчет амортизации и процентов с начала срока исполнения
            if (payment.getDate().equals(beginDate)) {
                var amr = calculateAmr(amount, sumOfInterestDay);
                amortizations.add(new AmrDto(amr, payment.getDate()));
                sumLiabilities = sumLiabilities.subtract(amr);

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
                sumLiabilities = sumLiabilities.subtract(amr);
                BigDecimal sumOfInterestMonth = calculatePerMonth(sumLiabilities, interestRate, daysOfMonth);

                interestExpenses.add(new SapInterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1),
                        sumOfInterestMonth,
                        daysOfMonth,
                        amr,
                        amount));
            }
        }

        //Общая сумма амортизации за весь срок исполнения
        sum = sumOfMonthAmr(amortizations);
        //Общая сумма процентов за 1 день всего срока исполнения
        sumPerDay = sumOfPerDay(interestExpenses);
        //Сумма процентов со 2 числа по конец месяца
        sumPerMonth = sumOfPerMonth(interestExpenses);

        log.info("Calculation of interest expenses was successful");

        return new CalculationsSapDto(interestExpenses, sum, sumPerDay, sumPerMonth);
    }

    private BigDecimal calculateNewDataFromPreviousMonth(List<SapInterestExpenseDto> interestExpenses, Double amount, BigDecimal sumOfInterestDay) {
        BigDecimal amr = BigDecimal.ZERO;
        for (SapInterestExpenseDto expense : interestExpenses) {
            if (expense.getDays() > 1) {
                amr = BigDecimal.valueOf(amount).subtract(expense.getInterestExpense()).subtract(sumOfInterestDay);
            }
        }
        return amr.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAmr(Double amount, BigDecimal sumOfInterestDay) {
        var amr = BigDecimal.valueOf(amount).subtract(sumOfInterestDay);
        return amr.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumOfPerMonth(List<SapInterestExpenseDto> interestExpenses) {
        return interestExpenses.stream()
                .filter(x -> x.getDays() == 1)
                .map(SapInterestExpenseDto::getInterestExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumOfPerDay(List<SapInterestExpenseDto> interestExpenses) {
        return interestExpenses.stream()
                .filter(x -> x.getDays() > 1)
                .map(SapInterestExpenseDto::getInterestExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumOfMonthAmr(List<AmrDto> amortizations) {
        return amortizations.stream()
                .map(AmrDto::getSumAmr)
                .map(x -> x.setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePerMonth(BigDecimal sumLiabilities, BigDecimal interestRate, int daysOfMonth) {
        var sumInterestMonth = sumLiabilities
                .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                .multiply(BigDecimal.valueOf(daysOfMonth))
                .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return sumInterestMonth.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePerDay(BigDecimal sumLiabilities, BigDecimal interestRate, int firstDayInMonth) {
        var sumOfInterestDay = sumLiabilities
                .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                .multiply(BigDecimal.valueOf(firstDayInMonth))
                .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return sumOfInterestDay.setScale(2, RoundingMode.HALF_UP);
    }


}