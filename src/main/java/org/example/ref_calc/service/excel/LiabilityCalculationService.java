package org.example.ref_calc.service.excel;

import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.excel.PaymentDto;

import java.util.List;

public interface LiabilityCalculationService {
    List<LiabilityDto> calculateLiabilities(List<PaymentDto> payments);
}
