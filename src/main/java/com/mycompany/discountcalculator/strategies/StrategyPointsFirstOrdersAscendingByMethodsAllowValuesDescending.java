
package com.mycompany.discountcalculator.strategies;

import com.mycompany.discountcalculator.Combination;
import com.mycompany.discountcalculator.DiscountCalculatorApplication;
import com.mycompany.discountcalculator.Order;
import com.mycompany.discountcalculator.PaymentMethod;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author anita
 */
public class StrategyPointsFirstOrdersAscendingByMethodsAllowValuesDescending implements Strategy {
    @Override
    public String getName(){
        return "Sortowanie rosnace zamowien po ilosci dostepnych metod i malejace metod, punkty calosciowe przed kartami, wartosci zamowien malejaco";
    }
    
    @Override
    public void apply(List<Order> orders, List<PaymentMethod> paymentMethods, Combination strategyResult){
        strategyResult.totalDiscount = BigInteger.ZERO;

        orders.sort(
            Comparator.comparingInt((Order o) -> o.promotions.size())
              .thenComparing(Order::getValue, Comparator.reverseOrder()));
        
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());

        for (Order order : orders) {
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForPointsFullValue(order, method, strategyResult)) break;
            }
            
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForCards(order, method, strategyResult)) continue;
            }
            
            for (PaymentMethod method : paymentMethods) {
                if (DiscountCalculatorApplication.checkForPointsPartialValueAlt(order, method, strategyResult)) continue;
            }
        }
    }
    
}