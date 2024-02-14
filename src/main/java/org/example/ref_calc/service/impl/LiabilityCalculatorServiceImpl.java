package org.example.ref_calc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.LiabilityCalculatorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LiabilityCalculatorServiceImpl implements LiabilityCalculatorService {
    @Override
    public List<LiabilityDto> calculateLiabilities(List<PaymentDto> payments) {
        log.info("Attempt to calculate liabilities");

        List<LiabilityDto> liabilities = new ArrayList<>();
        try {
            double totalPayments = payments.stream().mapToDouble(PaymentDto::getAmount).sum();
            liabilities.add(new LiabilityDto(totalPayments));

            for (int i = 0; i < payments.size(); i++) {
                double currentLiability = totalPayments - payments.subList(0, i + 1).stream().mapToDouble(PaymentDto::getAmount).sum();
                var dateLiability = payments.get(i).getDate();
                liabilities.add(new LiabilityDto(dateLiability, currentLiability));
            }
            log.info("Total amount of liabilities: {}", (double) Math.round(totalPayments * 100.0) / 100);
        } catch (ArithmeticException e) {
            log.error("Liability calculation error: {}", e.getMessage());
        }

        return liabilities;
    }
}
