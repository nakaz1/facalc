package org.example.ref_calc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterestExpenseDto {

    private LocalDate date;
    private double interestExpense;
    private int daysInMonth;
}
