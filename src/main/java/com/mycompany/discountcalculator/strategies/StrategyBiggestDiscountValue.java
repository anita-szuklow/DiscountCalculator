package com.mycompany.discountcalculator.strategies;

import com.mycompany.discountcalculator.Combination;
import com.mycompany.discountcalculator.DiscountCalculatorApplication;
import com.mycompany.discountcalculator.Order;
import com.mycompany.discountcalculator.PaymentMethod;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StrategyBiggestDiscountValue implements Strategy {
    @Override
    public String getName(){
        return "Sortowanie kandydatow po najwiekszym mozliwym jednostkowym rabacie do uzyskania";
    }
    @Override
    public void apply(List<Order> orders, List<PaymentMethod> paymentMethods, Combination strategyResult){
        strategyResult.totalDiscount = BigInteger.ZERO;
    
    class Candidate{
    Order order;
    PaymentMethod paymentMethod;
    BigInteger discount;
    
        Candidate(Order order, PaymentMethod paymentMethod, BigInteger discount){
         this.order = order;
         this.paymentMethod = paymentMethod;
         this.discount = discount;
        }
    }
    
    List<Candidate> candidates = new ArrayList<>();
    
    for (Order order : orders){
        for(PaymentMethod paymentMethod : paymentMethods){
            if (order.value.equals(BigInteger.ZERO)) continue;
            if (!order.promotions.contains(paymentMethod.id)) continue;
            if (order.value.compareTo(paymentMethod.limit) > 0) continue;
            
            BigInteger discount = order.value.
                    multiply(BigInteger.valueOf(paymentMethod.discount)).divide(BigInteger.valueOf(100));
            candidates.add(new Candidate(order, paymentMethod, discount));
        }
    }
    candidates.sort((a,b) -> b.discount.compareTo(a.discount));
     
     for (Candidate candidate : candidates){
         if(candidate.order.value.equals(BigInteger.ZERO)) continue;
         if(candidate.order.value.compareTo(candidate.paymentMethod.limit) > 0) continue;
         
         BigInteger discount = candidate.discount;
         BigInteger dueAmount = candidate.order.value
                 .multiply(BigInteger.valueOf(100 - candidate.paymentMethod.discount)).divide(BigInteger.valueOf(100));
         
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
