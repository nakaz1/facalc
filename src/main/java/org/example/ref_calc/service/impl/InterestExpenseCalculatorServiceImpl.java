package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.InterestExpenseCalculatorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InterestExpenseCalculatorServiceImpl implements InterestExpenseCalculatorService {
    @Override
    public List<InterestExpenseDto> calculateInterestExpenses(List<PaymentDto> payments, double interestRate, List<LiabilityDto> liabilities) {
        log.info("Attempt to calculate interest expense");
        List<InterestExpenseDto> interestExpenses = new ArrayList<>();
        try {
            int liabilityIndex = 0;
            for (PaymentDto payment : payments) {
                double sumLiabilities = liabilities.get(liabilityIndex).getLiability();
                liabilityIndex++;
                var daysInMonth = payment.getDate().lengthOfMonth() - 1;
                var firstDayInMonth = payment.getDate().lengthOfMonth() - daysInMonth;
                double expenseForMonth = (sumLiabilities * interestRate * daysInMonth) / payment.getDate().lengthOfYear();
                double expenseForDay = (sumLiabilities * interestRate * firstDayInMonth) / payment.getDate().lengthOfYear();
                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(1), expenseForDay, daysInMonth));
                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysInMonth + 1), expenseForMonth, daysInMonth));
            }
            log.info("Calculation of interest expenses was successful");

        } catch (ArithmeticException e) {
            log.error("Error occurred while calculating interest expense: {}", e.getMessage());
        }
        return interestExpenses;
    }
}
