package com.mycompany.discountcalculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;

public class PaymentMethod {
    public String id;
    public int discount;
    public BigInteger limit;
    
    public PaymentMethod(){
    };
    
    public PaymentMethod(PaymentMethod newMethod){
        this.id = newMethod.id;
        this.discount = newMethod.discount;
        this.limit = newMethod.limit;
    };
    
    @JsonProperty("limit")
    public void setLimit(BigDecimal limitFromJson){
        this.limit = limitFromJson.multiply(BigDecimal.valueOf(100)).toBigInteger();
    }
    
    public BigInteger getLimit(){
        return limit;
    }
    
    public int getDiscount(){
        return discount;
    }
    
    @Override
    public String toString(){
        return "Id: " + id + ", discount: " + discount + ", limit: " + limit; 
    }
//    
//    public void reset(){
//        this.limit = initialLimit;
//    }
//    public void setInitial(){
//        this.initialLimit = limit;
//    }
//    
}
