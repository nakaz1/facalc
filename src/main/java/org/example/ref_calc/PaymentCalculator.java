//package org.example.ref_calc;
//
//
//import org.example.ref_calc.entity.PaymentEntity;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PaymentCalculator {
//
//    public static List<PaymentEntity> calculateAFPP(LocalDate startDate, LocalDate endDate, double interestRate, double amount, LocalDate paymentDate) {
//        System.out.println("----------АФПП------------");
//        List<PaymentEntity> payments = new ArrayList<>();
//
//        System.out.println("Дата начала дог. отношений: " + startDate);
//
//        // Расчет платежа с начала периода до первого платежа
//        double initialPayment = amount / Math.pow(1 + interestRate, (double) ChronoUnit.DAYS.between(startDate, paymentDate) / paymentDate.lengthOfYear());
//        PaymentEntity initial = new PaymentEntity(paymentDate, initialPayment);
//        payments.add(initial);
//
//        System.out.println("Дата: " + paymentDate + " Платеж: " + initialPayment);
//
//        // Проходим через каждый месяц и выполняем расчеты
//        LocalDate currentDate = paymentDate.plusMonths(1); // Начинаем с первого месяца после первого платежа
//        while (currentDate.isBefore(endDate) || currentDate.equals(endDate)) {
//            // Расчет платежа на текущую дату
//            double paymentAmount = amount / Math.pow(1 + interestRate, (double) ChronoUnit.DAYS.between(startDate, currentDate) / paymentDate.lengthOfYear());
//            PaymentEntity payment = new PaymentEntity(currentDate, paymentAmount);
//            payments.add(payment);
//            System.out.println("Дата: " + currentDate + " Платеж: " + paymentAmount);
//
//            // Переход к следующему месяцу
//            currentDate = currentDate.plusMonths(1);
//        }
//
//        // Расчет платежа на последний день периода
//        double finalPayment = amount / 30;
//        PaymentEntity finalPaymentObj = new PaymentEntity(endDate, finalPayment);
//        payments.add(finalPaymentObj);
//        System.out.println("Дата: " + endDate + " Платеж: " + finalPayment);
//
//        double totalPayments = payments.stream().mapToDouble(PaymentEntity::getAmount).sum();
//        var roundTotalPayments = (double) Math.round(totalPayments * 100.0) / 100;
//        System.out.println("Общая сумма выплат: " + totalPayments);
//        System.out.println("Общая сумма выплат: " + roundTotalPayments);
//
//        return payments;
//    }
//
//    public static List<Double> calculateLiabilities(List<PaymentEntity> payments) {
//        System.out.println("----------Обязательства------------");
//
//        List<Double> liabilities = new ArrayList<>();
//        double totalPayments = payments.stream().mapToDouble(PaymentEntity::getAmount).sum();
//        liabilities.add(totalPayments);
//
//        for (int i = 0; i < payments.size(); i++) {
//            double currentLiability = totalPayments - payments.subList(0, i + 1).stream().mapToDouble(PaymentEntity::getAmount).sum();
//            liabilities.add(currentLiability);
//            System.out.println("Обязательство за " + payments.get(i).getDate() + ": " + currentLiability);
//        }
//        return liabilities;
//    }
//
//    public static List<Double> calculateInterestExpenses(List<PaymentEntity> payments, double interestRate, List<Double> liabilities) {
//        System.out.println("----------Процентные расходы------------");
//
//        List<Double> interestExpenses = new ArrayList<>();
//        int liabilityIndex = 0;
//        for (PaymentEntity payment : payments) {
//            double sumLiabilities = liabilities.get(liabilityIndex);
//            liabilityIndex++;
//            var daysInMonth = payment.getDate().lengthOfMonth() - 1;
//            var firstDayInMonth = payment.getDate().lengthOfMonth() - daysInMonth;
//            double expenseForMonth = (sumLiabilities * interestRate * daysInMonth) / payment.getDate().lengthOfYear();
//            double expenseForDay = (sumLiabilities * interestRate * firstDayInMonth) / payment.getDate().lengthOfYear();
//            interestExpenses.add(expenseForMonth);
//            interestExpenses.add(expenseForDay);
//            System.out.println("Обязательства: " + sumLiabilities + " Дата: " + payment.getDate().withDayOfMonth(1) + " Процентные расходы за день: " + expenseForDay + " Количество дней: " + firstDayInMonth);
//            System.out.println("Обязательства: " + sumLiabilities + " Дата: " + payment.getDate().withDayOfMonth(daysInMonth + 1) + " Процентные расходы за месяц: " + expenseForMonth + " Количество дней: " + daysInMonth);
//        }
//        return interestExpenses;
//    }
//
//    public static void main(String[] args) {
//        // Заданные данные
//        LocalDate startDate = LocalDate.of(2023, 1, 1);  // Дата начала
//        LocalDate endDate = LocalDate.of(2025, 1, 1);    // Дата конца
//        double interestRate = 8.198765 / 100;                // Процентная ставка
//        double amount = 103.00;                        // Сумма
//        LocalDate paymentDate = LocalDate.of(2023, 1, 15); // Дата платежа
//
//        // Выполнение расчетов АФПП
//        List<PaymentEntity> payments = calculateAFPP(startDate, endDate, interestRate, amount, paymentDate);
//
//        //Обязательства
//        List<Double> liabilities = calculateLiabilities(payments);
//
//        // Выполнение расчетов процентных расходов
//        List<Double> interestExpenses = calculateInterestExpenses(payments, interestRate, liabilities);
//    }
//}
//
