package com.mycompany.discountcalculator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Combination {
    public BigInteger totalDiscount;
    public Map<String, BigInteger> finalPayments;
    
    public Combination(){
        this.totalDiscount = BigInteger.ZERO;
        this.finalPayments = new HashMap<>();
    }
    
    public Combination(BigInteger totalDiscount){
        this.totalDiscount = totalDiscount;
        this.finalPayments = new HashMap<>();
    }
    
    public void addPaymentToMethod(String id, BigInteger amount){
        if (finalPayments.containsKey(id)) {
        BigInteger current = finalPayments.get(id);
        finalPayments.put(id, current.add(amount));
    } else {
        finalPayments.put(id, amount);
    }
    }    
    public void finalResult() {
    for (Map.Entry<String, BigInteger> entry : finalPayments.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue()+"/n");
    }
}
}
