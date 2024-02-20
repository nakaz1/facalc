package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.AmrDto;
import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.InterestExpenseCalculationServiceV2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InterestExpenseCalculationServiceImplv2 implements InterestExpenseCalculationServiceV2 {

    private static final int DAYS_OF_YEAR = 366;

    @Override
    public List<InterestExpenseDto> calculateInterestExpenses(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities, Double amount) {
        log.info("Attempt to calculate interest expense");
        List<InterestExpenseDto> interestExpenses = new ArrayList<>();
        List<AmrDto> amortizations = new ArrayList<>();
        try {
            for (int i = 1; i < payments.size(); i++) {
                PaymentDto payment = payments.get(i);
                var sumLiabilities = liabilities.get(i - 1).getLiability();
                var daysOfMonth = payment.getDate().lengthOfMonth() - 1;
                var firstDayInMonth = payment.getDate().lengthOfMonth() - daysOfMonth;

                BigDecimal sumOfInterestDay = sumLiabilities
                        .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                        .multiply(BigDecimal.valueOf(firstDayInMonth))
                        .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(1), sumOfInterestDay, 1));


                if (i == 1) {
                    var amr = BigDecimal.valueOf(amount).subtract(sumOfInterestDay);
                    amortizations.add(new AmrDto(amr, payment.getDate()));
                    var lia = sumLiabilities.subtract(amr);
                    BigDecimal sumOfInterestMonth = lia
                            .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                            .multiply(BigDecimal.valueOf(daysOfMonth))
                            .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1), sumOfInterestMonth, daysOfMonth));

                } else {
                    var amr1 = BigDecimal.valueOf(amount).subtract(interestExpenses.get(1).getInterestExpense()).subtract(sumOfInterestDay);
                    amortizations.add(new AmrDto(amr1, payment.getDate()));
                    var lia1 = sumLiabilities.subtract(amr1);
                    BigDecimal sumOfInterestMonth = lia1
                            .multiply(interestRate.divide(BigDecimal.valueOf(100)))
                            .multiply(BigDecimal.valueOf(daysOfMonth))
                            .divide(BigDecimal.valueOf(DAYS_OF_YEAR), RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysOfMonth + 1), sumOfInterestMonth, daysOfMonth));
                }

            }

            log.info("Calculation of interest expenses was successful");

        } catch (ArithmeticException e) {
            log.error("Error occurred while calculating interest expense: {}", e.getMessage());
        }
        return interestExpenses;
    }


}
