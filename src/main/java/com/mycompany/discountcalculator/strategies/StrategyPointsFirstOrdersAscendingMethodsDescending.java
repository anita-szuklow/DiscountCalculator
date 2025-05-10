package com.mycompany.discountcalculator.strategies;

import com.mycompany.discountcalculator.Combination;
import com.mycompany.discountcalculator.DiscountCalculatorApplication;
import com.mycompany.discountcalculator.Order;
import com.mycompany.discountcalculator.PaymentMethod;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

public class StrategyPointsFirstOrdersAscendingMethodsDescending implements Strategy {
    @Override
    public String getName(){
        return "Sortowanie malejaca zamowien i metod, punkty calosciowe przed kartami";
    }
    
    @Override
    public void apply(List<Order> orders, List<PaymentMethod> paymentMethods, Combination strategyResult){
        strategyResult.totalDiscount = BigInteger.ZERO;

        orders.sort(Comparator.comparing(Order::getValue));
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());

        for (Order order : orders) {
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForPointsFullValue(order, method, strategyResult)) break;
            }
            
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForCards(order, method, strategyResult)) continue;
            }
            
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForPointsPartialValue(order, method, strategyResult)) break;
            }
        }
    }
    
}
