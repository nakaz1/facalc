package org.example.ref_calc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ref_calc.dto.CalculationRequestDto;
import org.example.ref_calc.dto.LiabilityDto;
import org.example.ref_calc.dto.excel.ExcelCalculationResponseDto;
import org.example.ref_calc.dto.excel.InterestExpenseDto;
import org.example.ref_calc.dto.excel.PaymentDto;
import org.example.ref_calc.dto.sap.CalculationsSapDto;
import org.example.ref_calc.dto.sap.SapAfppDto;
import org.example.ref_calc.dto.sap.SapCalculationResponseDto;
import org.example.ref_calc.service.excel.InterestExpenseCalculationService;
import org.example.ref_calc.service.excel.LiabilityCalculationService;
import org.example.ref_calc.service.excel.PaymentCalculationService;
import org.example.ref_calc.service.sap.InterestExpenseCalculationServiceV2;
import org.example.ref_calc.service.sap.LiabilityCalculationServiceV2;
import org.example.ref_calc.service.sap.PaymentCalculationServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    private final PaymentCalculationServiceV2 paymentCalculationServiceV2;
    private final LiabilityCalculationServiceV2 liabilityCalculationServiceV2;
    private final InterestExpenseCalculationServiceV2 interestExpenseCalculationServiceV2;


    @PostMapping("/calculate-excel")
    public ResponseEntity<ExcelCalculationResponseDto> calculateExcel(@RequestBody CalculationRequestDto request) {

        // Расчет АФПП
        List<PaymentDto> payments = paymentCalculationService.calculatePayments(request.getBeginDate(), request.getEndDate(), request.getInterestRate(), request.getAmount(), request.getPaymentDate());

        // Расчет обязательств
        List<LiabilityDto> liabilities = liabilityCalculationService.calculateLiabilities(payments);
        var liability = liabilities.get(0).getLiability().setScale(2, RoundingMode.HALF_UP);

        // Расчет процентные расходы
        List<InterestExpenseDto> interestExpenses = interestExpenseCalculationService.calculateInterestExpensesExcel(payments, request.getInterestRate(), liabilities);

        ExcelCalculationResponseDto calculationResponseDto = new ExcelCalculationResponseDto();
        calculationResponseDto.setPayments(payments);
        calculationResponseDto.setAfpp(liability);
        calculationResponseDto.setInterestExpenses(interestExpenses);

        return ResponseEntity.ok(calculationResponseDto);
    }

    @PostMapping("/calculate-sap")
    public ResponseEntity<?> calculateSap(@Validated @RequestBody CalculationRequestDto request, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        try {
            //Расчет АФПП помесячно
            List<SapAfppDto> payments = paymentCalculationServiceV2.calculatePayments(request.getBeginDate(), request.getEndDate(), request.getInterestRate(), request.getAmount(), request.getPaymentDate());

            // Сумма актива помесячно
            List<LiabilityDto> liabilities = liabilityCalculationServiceV2.calculateLiabilities(payments);

            // Общая сумма актива на основе АФПП
            var afpp = liabilities.get(0).getLiability().setScale(2, RoundingMode.HALF_UP);

            //Расчет амортизации и процентов (день, месяц)
            CalculationsSapDto calculations = interestExpenseCalculationServiceV2.calculateInterestExpensesSap(payments, request.getInterestRate(), liabilities, request.getAmount(), request.getBeginDate(), request.getEndDate());

            SapCalculationResponseDto resp = new SapCalculationResponseDto();
            resp.setAfpp(afpp);
            resp.setCalculations(calculations);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            // Логирование ошибки
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during calculation.");
        }
    }
}
