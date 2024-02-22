package org.example.ref_calc.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SapInterestExpenseDto {
    private LocalDate date;
    private BigDecimal interestExpense;
    private int days;
    private BigDecimal amr;
    private Double amount;
}
