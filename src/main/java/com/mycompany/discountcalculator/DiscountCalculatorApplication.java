package com.mycompany.discountcalculator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.discountcalculator.strategies.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.List;

public class DiscountCalculatorApplication {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java DiscountCalculatorApplication <file1> <file2>");
            System.exit(1);              
        }
        String ordersPath = args[0];
        String methodsPath = args[1];

        try{
        File ordersFile = new File(ordersPath);
        File paymentMethodsFile = new File(methodsPath);
        
        DataResetter resetter1 = resetData(ordersPath, methodsPath);
        List<Order> orders = resetter1.orders;
        List<PaymentMethod> paymentMethods = resetter1.paymentMethods;

        Combination masterCombination = new Combination();
        Strategy bestStrategy = null;
        
        orders.forEach(System.out::println);
        paymentMethods.forEach(System.out::println);
        
        List<Strategy> strategies = Arrays.asList(
                new StrategyCardsFirstOrdersDescendingMethodsDescending(),
                new StrategyPointsFirstOrdersAscendingMethodsDescending(),
                new StrategyPointsFirstOrdersDescendingMethodsDescending()
        );
        
        for (Strategy strategy : strategies){
            DataResetter setter2 = resetData(ordersPath, methodsPath);
            
            Combination tryCombination = new Combination();
            strategy.apply(setter2.orders, setter2.paymentMethods, tryCombination);
            
            System.out.println("Strategia: " + strategy.getName() + "rabat calkowity: " + tryCombination.totalDiscount);
            
            if (tryCombination.totalDiscount.compareTo(masterCombination.totalDiscount) > 0) {
                masterCombination = tryCombination;
                bestStrategy = strategy;
            }            
        }
        //uruchamiam ponownie najlepsza strategie, aby oplacic wszystkie kwoty zamowien z rabatami
        DataResetter finalResetter = resetData(ordersPath, methodsPath);
        Combination finalCombination = new Combination();
        
        System.out.println("/nPo swiezym zaladowaniu:/n");
        finalResetter.orders.forEach(System.out::println);
        finalResetter.paymentMethods.forEach(System.out::println);
        System.out.println("Suma rabatow master: " + masterCombination.totalDiscount.toString());
        System.out.println("Suma rabatow final: " + finalCombination.totalDiscount.toString());
        
        bestStrategy.apply(finalResetter.orders, finalResetter.paymentMethods, finalCombination);
        
        System.out.println("/nPo oplaceniu rabatow:/n");
        finalResetter.orders.forEach(System.out::println);
        finalResetter.paymentMethods.forEach(System.out::println);
        System.out.println("Suma rabatow master: " + masterCombination.totalDiscount.toString());
        System.out.println("Suma rabatow final: " + finalCombination.totalDiscount.toString());       
        
        //oplacenie pozostalych niezrabatowanych kwot zamowien
        for(Order order : finalResetter.orders){
            for(PaymentMethod paymentMethod : finalResetter.paymentMethods){
            if(!order.value.equals(BigInteger.ZERO)){
                    if(paymentMethod.limit.compareTo(order.value) > 0){
                        paymentMethod.limit = paymentMethod.limit.subtract(order.value);
                        finalCombination.addPaymentToMethod(paymentMethod.id, order.value);
                        order.value = BigInteger.ZERO;
                        break;
                    }
                    if(paymentMethod.limit.compareTo(order.value) < 0){
                        order.value.subtract(paymentMethod.limit);
                        finalCombination.addPaymentToMethod(paymentMethod.id, paymentMethod.limit);
                        paymentMethod.limit = BigInteger.ZERO;
                        continue;
                    }
                }
            }
        }
        
        System.out.println("/nPo oplaceniu wszystkiego:/n");
        finalResetter.orders.forEach(System.out::println);
        finalResetter.paymentMethods.forEach(System.out::println);
        System.out.println("Suma rabatow master: " + masterCombination.totalDiscount.toString());
        System.out.println("Suma rabatow final: " + finalCombination.totalDiscount.toString());
        
        finalCombination.finalResult();
        }
        catch(IOException exception){
            System.err.println(exception.getMessage());
        }       
    }
    
    public static boolean checkForPointsFullValue(Order order, PaymentMethod paymentMethod, Combination tryCombination){
         if (!paymentMethod.id.equals("PUNKTY") || order.value.compareTo(paymentMethod.limit) > 0 || order.value.equals(BigInteger.ZERO))
            return false;
        if (paymentMethod.id.equals("PUNKTY") && (order.value.compareTo(paymentMethod.limit) <= 0) && !order.value.equals(BigInteger.ZERO)){
                    BigInteger discount1 = order.value.multiply(BigInteger.valueOf(paymentMethod.discount)).divide(BigInteger.valueOf(100));           
                    BigInteger dueAmount = order.value.multiply((BigInteger.valueOf(100).subtract(BigInteger.valueOf(paymentMethod.discount)))).divide(BigInteger.valueOf(100));           
                    tryCombination.totalDiscount = tryCombination.totalDiscount.add(discount1);
                    tryCombination.addPaymentToMethod(paymentMethod.id, dueAmount);
                    paymentMethod.limit = paymentMethod.limit.subtract(dueAmount);
                    order.value = BigInteger.valueOf(0);
                }
            return true;
    
    }
    
    public static boolean checkForCards(Order order, PaymentMethod paymentMethod, Combination tryCombination){
        if (paymentMethod.id.equals("PUNKTY")) return false;
        if (order.promotions.contains(paymentMethod.id) 
                        && (order.value.compareTo(paymentMethod.limit) <= 0) && !order.value.equals(BigInteger.ZERO)){
                    BigInteger discount1 = order.value.multiply(BigInteger.valueOf(paymentMethod.discount)).divide(BigInteger.valueOf(100));           
                    BigInteger dueAmount = order.value.multiply((BigInteger.valueOf(100).subtract(BigInteger.valueOf(paymentMethod.discount)))).divide(BigInteger.valueOf(100));  
                    tryCombination.totalDiscount = tryCombination.totalDiscount.add(discount1);
                    tryCombination.addPaymentToMethod(paymentMethod.id, dueAmount);
                    paymentMethod.limit = paymentMethod.limit.subtract(dueAmount);
                    order.value = BigInteger.valueOf(0);
                }
                return true;
    }
    
    public static boolean checkForPointsPartialValue(Order order, PaymentMethod paymentMethod, Combination tryCombination){
        if (!paymentMethod.id.equals("PUNKTY")
                        || (order.value.divide(BigInteger.valueOf(10)).compareTo(paymentMethod.limit)>=0)
                        || order.value.equals(BigInteger.ZERO))
            return false;
        if (paymentMethod.id.equals("PUNKTY") && (order.value.compareTo(paymentMethod.limit) > 0)
                        && (order.value.divide(BigInteger.valueOf(10)).compareTo(paymentMethod.limit)<=0)
                        && !order.value.equals(BigInteger.ZERO)){
                    BigInteger discount1 = order.value.divide(BigInteger.valueOf(10));                    
                    tryCombination.totalDiscount = tryCombination.totalDiscount.add(discount1);
                    tryCombination.addPaymentToMethod(paymentMethod.id, paymentMethod.limit);
                    order.value = order.value.subtract(discount1);
                    order.value = order.value.subtract(paymentMethod.limit);
                    paymentMethod.limit = BigInteger.ZERO;                    
                }
        return true;
    }

    public static DataResetter resetData(String ordersPath, String methodsPath) throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    List<Order> orders = mapper.readValue(new File(ordersPath), new TypeReference<>() {});
    List<PaymentMethod> methods = mapper.readValue(new File(methodsPath), new TypeReference<>() {});
    return new DataResetter(orders, methods);
}
        
    
    
}
