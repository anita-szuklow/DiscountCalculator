package com.mycompany.discountcalculator;

import com.mycompany.discountcalculator.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class DiscountTest {

    static class TestCase {
        BigInteger orderValue;
        int discountPercent;
        BigInteger methodLimit;
        boolean expectedApplicable;
        BigInteger expectedRemainingLimit;
        BigInteger expectedTotalDiscount;

        TestCase(BigInteger orderValue, int discountPercent, BigInteger methodLimit,
                 boolean expectedApplicable, BigInteger expectedRemainingLimit, BigInteger expectedTotalDiscount) {
            this.orderValue = orderValue;
            this.discountPercent = discountPercent;
            this.methodLimit = methodLimit;
            this.expectedApplicable = expectedApplicable;
            this.expectedRemainingLimit = expectedRemainingLimit;
            this.expectedTotalDiscount = expectedTotalDiscount;
        }
    }

    static Stream<TestCase> testCases() {
        return Stream.of(
            new TestCase(new BigInteger("10000"), 10, new BigInteger("10000"), true, new BigInteger("1000"), new BigInteger("1000")),
            new TestCase(new BigInteger("5000"), 20, new BigInteger("3000"), false, new BigInteger("3000"), BigInteger.ZERO),
            new TestCase(new BigInteger("12000"), 25, new BigInteger("12000"), true, new BigInteger("3000"), new BigInteger("3000"))
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testCheckForCards(TestCase testCase) {
        Order order = new Order();
        order.value = testCase.orderValue;
        order.promotions = List.of("VISA");

        PaymentMethod method = new PaymentMethod();
        method.id = "VISA";
        method.discount = testCase.discountPercent;
        method.limit = testCase.methodLimit;

        Combination result = new Combination(BigInteger.ZERO);

        boolean applied = DiscountCalculatorApplication.checkForCards(order, method, result);

        assertEquals(testCase.expectedApplicable, applied);
        assertEquals(testCase.expectedRemainingLimit, method.limit);
        assertEquals(testCase.expectedTotalDiscount, result.totalDiscount);
    }
    
    // ----------- Test checkForPointsFullValue ------------

    static Stream<ArgumentsFull> pointsFullCases() {
        return Stream.of(
                new ArgumentsFull("10000", "9000", 10, false, "10000", "9000", "0"),
                new ArgumentsFull("10000", "10000", 10, true, "0", "1000", "1000"),
                new ArgumentsFull("10000", "5000", 10, false, "10000", "5000", "0")
        );
    }

    @ParameterizedTest
    @MethodSource("pointsFullCases")
    void testCheckForPointsFullValue(ArgumentsFull args) {
        Order order = new Order();
        order.value = new BigInteger(args.orderValue);
        order.promotions = List.of("PUNKTY");

        PaymentMethod method = new PaymentMethod();
        method.id = "PUNKTY";
        method.discount = args.discount;
        method.limit = new BigInteger(args.methodLimit);

        Combination combo = new Combination(BigInteger.ZERO);

        boolean result = DiscountCalculatorApplication.checkForPointsFullValue(order, method, combo);

        assertEquals(args.expectedResult, result);
        assertEquals(new BigInteger(args.expectedOrder), order.value);
        assertEquals(new BigInteger(args.expectedLimit), method.limit);
        assertEquals(new BigInteger(args.expectedDiscount), combo.totalDiscount);
    }

    // ----------- Test checkForPointsPartialValue ------------

    static Stream<ArgumentsFull> pointsPartialCases() {
        return Stream.of(
                new ArgumentsFull("10000", "3000", 10, true, "6000", "0", "1000"),
                new ArgumentsFull("8000", "1000", 10, true, "6200", "0", "800")
        );
    }

    @ParameterizedTest
    @MethodSource("pointsPartialCases")
    void testCheckForPointsPartialValue(ArgumentsFull args) {
        Order order = new Order();
        order.value = new BigInteger(args.orderValue);
        order.promotions = List.of("PUNKTY");

        PaymentMethod method = new PaymentMethod();
        method.id = "PUNKTY";
        method.discount = args.discount;
        method.limit = new BigInteger(args.methodLimit);

        Combination combo = new Combination(BigInteger.ZERO);

        boolean result = DiscountCalculatorApplication.checkForPointsPartialValue(order, method, combo);

        assertEquals(args.expectedResult, result);
        assertEquals(new BigInteger(args.expectedOrder), order.value);
        assertEquals(new BigInteger(args.expectedLimit), method.limit);
        assertEquals(new BigInteger(args.expectedDiscount), combo.totalDiscount);
    }

    // ----------- Test final fallback payment ------------

    static Stream<ArgumentsFinalFallback> finalFallbackCases() {
        return Stream.of(
                new ArgumentsFinalFallback("5000", "6000", "0", "1000"),
                new ArgumentsFinalFallback("7000", "4000", "3000", "0")
        );
    }

    @ParameterizedTest
    @MethodSource("finalFallbackCases")
    void testFinalFallbackPayment(ArgumentsFinalFallback args) {
        Order order = new Order();
        order.value = new BigInteger(args.orderValue);

        PaymentMethod method = new PaymentMethod();
        method.id = "X";
        method.limit = new BigInteger(args.methodLimit);

        if (!order.value.equals(BigInteger.ZERO)) {
            if (method.limit.compareTo(order.value) >= 0) {
                method.limit = method.limit.subtract(order.value);
                order.value = BigInteger.ZERO;
            } else {
                order.value = order.value.subtract(method.limit);
                method.limit = BigInteger.ZERO;
            }
        }

        assertEquals(new BigInteger(args.expectedOrder), order.value);
        assertEquals(new BigInteger(args.expectedLimit), method.limit);
    }

    // ----------- Test recordPaymentUsage ------------

    static Stream<ArgumentsUsage> usageCases() {
        return Stream.of(
                new ArgumentsUsage(Map.of("PUNKTY", "1000", "VISA", "500"))
        );
    }

    @ParameterizedTest
    @MethodSource("usageCases")
    void testRecordPaymentUsage(ArgumentsUsage args) {
        Combination combo = new Combination(BigInteger.ZERO);

        args.usage.forEach((k, v) -> combo.addPaymentToMethod(k, new BigInteger(v)));
        args.usage.forEach((k, v) -> assertEquals(new BigInteger(v), combo.finalPayments.get(k)));
    }

    // ----------- Argument helper classes ------------

    static class ArgumentsFull {
        String orderValue, methodLimit;
        int discount;
        boolean expectedResult;
        String expectedOrder, expectedLimit, expectedDiscount;

        ArgumentsFull(String orderValue, String methodLimit, int discount, boolean expectedResult,
                      String expectedOrder, String expectedLimit, String expectedDiscount) {
            this.orderValue = orderValue;
            this.methodLimit = methodLimit;
            this.discount = discount;
            this.expectedResult = expectedResult;
            this.expectedOrder = expectedOrder;
            this.expectedLimit = expectedLimit;
            this.expectedDiscount = expectedDiscount;
        }
    }

    static class ArgumentsFinalFallback {
        String orderValue, methodLimit, expectedOrder, expectedLimit;

        ArgumentsFinalFallback(String orderValue, String methodLimit, String expectedOrder, String expectedLimit) {
            this.orderValue = orderValue;
            this.methodLimit = methodLimit;
            this.expectedOrder = expectedOrder;
            this.expectedLimit = expectedLimit;
        }
    }

    static class ArgumentsUsage {
        Map<String, String> usage;

        ArgumentsUsage(Map<String, String> usage) {
            this.usage = usage;
        }
    }
}

