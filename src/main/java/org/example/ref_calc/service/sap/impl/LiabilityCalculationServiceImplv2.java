package org.example.ref_calc.service.sap.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.sap.SapAfppDto;
import org.example.ref_calc.service.sap.LiabilityCalculationServiceV2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LiabilityCalculationServiceImplv2 implements LiabilityCalculationServiceV2 {
    @Override
    public List<LiabilityDto> calculateLiabilities(List<SapAfppDto> payments) {
        log.info("Attempt to calculate liabilities");

        List<LiabilityDto> liabilities = new ArrayList<>();
        try {
            var beginDateLiability = payments.get(0).getDate();
            var totalPayments = payments.stream()
                    .map(SapAfppDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            liabilities.add(new LiabilityDto(beginDateLiability, totalPayments));

            for (int i = 1; i < payments.size(); i++) {
                BigDecimal currentLiability = totalPayments.subtract(payments.subList(0, i + 1)
                        .stream()
                        .map(SapAfppDto::getAmount)
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
