package org.example.ref_calc.service;

import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;

import java.util.List;

public interface LiabilityCalculatorService {
    List<LiabilityDto> calculateLiabilities(List<PaymentDto> payments);
}
