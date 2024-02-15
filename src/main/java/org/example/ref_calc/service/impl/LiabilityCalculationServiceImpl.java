package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.LiabilityCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LiabilityCalculationServiceImpl implements LiabilityCalculationService {
    @Override
    public List<LiabilityDto> calculateLiabilities(List<PaymentDto> payments) {
        log.info("Attempt to calculate liabilities");

        List<LiabilityDto> liabilities = new ArrayList<>();
        try {
            var beginDateLiability = payments.get(0).getDate();
            var totalPayments = payments.stream()
                    .map(PaymentDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            liabilities.add(new LiabilityDto(beginDateLiability, totalPayments));

            for (int i = 1; i < payments.size(); i++) {
//                double currentLiability = totalPayments - payments.subList(0, i + 1).stream().mapToDouble(PaymentDto::getAmount).sum();
                BigDecimal currentLiability = totalPayments.subtract(payments.subList(0, i + 1)
                        .stream()
                        .map(PaymentDto::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

                var dateLiability = payments.get(i).getDate();
                liabilities.add(new LiabilityDto(dateLiability, currentLiability));
            }
            log.info("Total amount of liabilities: {}", (double) Math.round(totalPayments.doubleValue() * 100.0) / 100);
        } catch (ArithmeticException e) {
            log.error("Liability calculation error: {}", e.getMessage());
        }

        return liabilities;
    }
}
