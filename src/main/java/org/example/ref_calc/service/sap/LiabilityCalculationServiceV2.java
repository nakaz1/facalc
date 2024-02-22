package org.example.ref_calc.service.sap;

import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.sap.SapAfppDto;

import java.util.List;

public interface LiabilityCalculationServiceV2 {
    List<LiabilityDto> calculateLiabilities(List<SapAfppDto> payments);
}
