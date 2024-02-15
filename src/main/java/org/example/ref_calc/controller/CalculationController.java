package org.example.ref_calc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.CalculationRequestDto;
import org.example.ref_calc.dto.CalculationResponseDto;
import org.example.ref_calc.dto.InterestExpenseDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.PaymentDto;
import org.example.ref_calc.service.InterestExpenseCalculationService;
import org.example.ref_calc.service.LiabilityCalculationService;
import org.example.ref_calc.service.PaymentCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calculation")
@RequiredArgsConstructor
@Slf4j
public class CalculationController {

    private final PaymentCalculationService paymentCalculationService;
    private final LiabilityCalculationService liabilityCalculationService;
    private final InterestExpenseCalculationService interestExpenseCalculationService;

    @PostMapping("/calculate")

    public ResponseEntity<CalculationResponseDto> calculate(@RequestBody CalculationRequestDto request) {

        // Расчет АФПП
        List<PaymentDto> payments = paymentCalculationService.calculatePayments(request.getBeginDate(), request.getEndDate(), request.getInterestRate(), request.getAmount(), request.getPaymentDate());

        // Расчет обязательств
        List<LiabilityDto> liabilities = liabilityCalculationService.calculateLiabilities(payments);
        var tmp = liabilities.get(0).getLiability().setScale(2, RoundingMode.HALF_UP);

        // Расчет процентные расходы
        List<InterestExpenseDto> interestExpenses = interestExpenseCalculationService.calculateInterestExpenses(payments, request.getInterestRate(), liabilities);

        CalculationResponseDto calculationResponseDto = new CalculationResponseDto();
        calculationResponseDto.setPayments(payments);
        calculationResponseDto.setLiability(tmp);
        calculationResponseDto.setInterestExpenses(interestExpenses);

        return ResponseEntity.ok(calculationResponseDto);
    }

}
