//package org.example.ref_calc.service.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.ref_calc.dto.InterestExpenseDto;
//import org.example.ref_calc.dto.LiabilityDto;
//import org.example.ref_calc.dto.PaymentDto;
//import org.example.ref_calc.service.InterestExpenseCalculationService;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Slf4j
//public class InterestExpenseCalculationServiceImpl implements InterestExpenseCalculationService {
//    @Override
//    public List<InterestExpenseDto> calculateInterestExpenses(List<PaymentDto> payments, BigDecimal interestRate, List<LiabilityDto> liabilities) {
//        log.info("Attempt to calculate interest expense");
//        List<InterestExpenseDto> interestExpenses = new ArrayList<>();
//        try {
//            for (int i = 1; i < payments.size(); i++) {
//                PaymentDto payment = payments.get(i);
//                var sumLiabilities = liabilities.get(i - 1).getLiability();
//                var daysInMonth = payment.getDate().lengthOfMonth() - 1;
//                var firstDayInMonth = payment.getDate().lengthOfMonth() - daysInMonth;
//
//                BigDecimal expenseForMonth = sumLiabilities
//                        .multiply(interestRate)
//                        .multiply(BigDecimal.valueOf(daysInMonth))
//                        .divide(BigDecimal.valueOf(payment.getDate().lengthOfYear()), RoundingMode.HALF_UP);
//
//                BigDecimal expenseForDay = sumLiabilities
//                        .multiply(interestRate)
//                        .multiply(BigDecimal.valueOf(firstDayInMonth))
//                        .divide(BigDecimal.valueOf(payment.getDate().lengthOfYear()), RoundingMode.HALF_UP);
//
//                //todo убрать вывод за дату конца дог. отношений для корректного вывода
//                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(1), expenseForDay, 1));
//                interestExpenses.add(new InterestExpenseDto(payment.getDate().withDayOfMonth(daysInMonth + 1), expenseForMonth, daysInMonth));
//            }
//
//            log.info("Calculation of interest expenses was successful");
//
//        } catch (ArithmeticException e) {
//            log.error("Error occurred while calculating interest expense: {}", e.getMessage());
//        }
//        return interestExpenses;
//    }
//}
