package com.mycompany.discountcalculator.strategies;

import com.mycompany.discountcalculator.Combination;
import com.mycompany.discountcalculator.DiscountCalculatorApplication;
import com.mycompany.discountcalculator.Order;
import com.mycompany.discountcalculator.PaymentMethod;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StrategyBestDiscountEfficiency implements Strategy {
    @Override
    public String getName(){
        return "Sortowanie kandydatow po najwydajniejszym rabacie";
    }
    @Override
    public void apply(List<Order> orders, List<PaymentMethod> paymentMethods, Combination strategyResult){
        strategyResult.totalDiscount = BigInteger.ZERO;
    
    class Candidate{
    Order order;
    PaymentMethod paymentMethod;
    BigDecimal efficiency;
    
        Candidate(Order order, PaymentMethod paymentMethod, BigDecimal efficiency){
         this.order = order;
         this.paymentMethod = paymentMethod;
         this.efficiency = efficiency;
        }
    }
    
    List<Candidate> candidates = new ArrayList<>();
    
    for (Order order : orders){
        for(PaymentMethod paymentMethod : paymentMethods){
            if (order.value.equals(BigInteger.ZERO)) continue;
            if (!order.promotions.contains(paymentMethod.id)) continue;
            if (order.value.compareTo(paymentMethod.limit) > 0) continue;
            
            BigDecimal discount = new BigDecimal(order.value)
                    .multiply(BigDecimal.valueOf(paymentMethod.discount)).divide(BigDecimal.valueOf(100));
            BigDecimal efficiency = discount.divide(new BigDecimal(order.value), 6, RoundingMode.HALF_UP);
            candidates.add(new Candidate(order, paymentMethod, efficiency));
        }
    }
    candidates.sort((a,b) -> b.efficiency.compareTo(a.efficiency));
     
     for (Candidate candidate : candidates){
         if(candidate.order.value.equals(BigInteger.ZERO)) continue;
         if(candidate.order.value.compareTo(candidate.paymentMethod.limit) > 0) continue;
         
         BigInteger discount = candidate.order.value.multiply(BigInteger.valueOf(candidate.paymentMethod.discount)).divide(BigInteger.valueOf(100));
         BigInteger dueAmount = candidate.order.value.multiply(BigInteger.valueOf(100 - candidate.paymentMethod.discount)).divide(BigInteger.valueOf(100));
         
         strategyResult.totalDiscount = strategyResult.totalDiscount.add(discount);
         strategyResult.addPaymentToMethod(candidate.paymentMethod.id, dueAmount);
         candidate.paymentMethod.limit = candidate.paymentMethod.limit.subtract(dueAmount);
         candidate.order.value = BigInteger.ZERO;
     }
     
    orders.sort(Comparator.comparing(Order::getValue).reversed());
    for (Order order : orders) { 
        for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForPointsPartialValue(order, method, strategyResult)) continue;
            }
        }    
    }
}