package org.example.ref_calc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationRequestDto {
    private LocalDate beginDate;
    private LocalDate endDate;
    private BigDecimal interestRate;
    private double amount;
    private LocalDate paymentDate;
}
