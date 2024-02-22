package org.example.ref_calc.dto.excel;

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
public class InterestExpenseDto {

    private LocalDate date;
    private BigDecimal interestExpense;
    private int days;
}
