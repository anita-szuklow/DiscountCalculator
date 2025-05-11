package com.mycompany.discountcalculator;

import java.util.List;

public class DataResetter {
    public List<Order> orders;
    public List<PaymentMethod> paymentMethods;
    
    public DataResetter(List<Order> orders, List<PaymentMethod> paymentMethods){
        this.orders = orders;
        this.paymentMethods = paymentMethods;
    }    
}

