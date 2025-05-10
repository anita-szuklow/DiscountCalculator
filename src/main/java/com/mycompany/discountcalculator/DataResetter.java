/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.discountcalculator;

import java.util.List;

/**
 *
 * @author anita
 */
public class DataResetter {
    public List<Order> orders;
    public List<PaymentMethod> paymentMethods;
    
    public DataResetter(List<Order> orders, List<PaymentMethod> paymentMethods){
        this.orders = orders;
        this.paymentMethods = paymentMethods;
    }    
}

