package org.example.ref_calc.dto.sap;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SapCalculationResponseDto {
    private BigDecimal afpp;
    private CalculationsSapDto calculations;
}
