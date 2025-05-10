package com.mycompany.discountcalculator.strategies;

import com.mycompany.discountcalculator.*;
import java.util.List;


public interface Strategy {
    String getName();
    void apply(List<Order> orders, List<PaymentMethod> paymenetMethods, Combination result);
    
}
