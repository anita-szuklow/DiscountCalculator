package com.mycompany.discountcalculator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        
//        ObjectMapper myMapper = new ObjectMapper();
        
//        List<Order> unsortedOrders = myMapper.readValue(ordersFile, new TypeReference<>(){});
//        List<PaymentMethod> unsortedPaymentMethods = myMapper.readValue(paymentMethodsFile, new TypeReference<>(){});
        
//        List<Order> orders = new ArrayList<>(unsortedOrders);
        orders.sort(Comparator.comparing(Order::getValue).reversed());
//        
//        List<Order> ascendingOrders = new ArrayList<>(orders);
//        ascendingOrders.reversed();
//
//        List<PaymentMethod> paymentMethods = new ArrayList<>(unsortedPaymentMethods);
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
        
        Combination masterCombination = new Combination();
        Combination tryCombination = new Combination();
        
        orders.forEach(System.out::println);
        paymentMethods.forEach(System.out::println);
        
        
        for(Order order : orders){
            for(PaymentMethod paymentMethod : paymentMethods){
                 //sprawdzam, czy punkty moga w calosci oplacic kolejne zamowienia
                if(checkForPointsFullValue(order, paymentMethod, tryCombination)) break;
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                // sprawdzam pozostale metody oplacania zamowien
                if(checkForCards(order, paymentMethod, tryCombination)) continue;
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                // jesli nie da sie oplacic w calosci z rabatem to sprawdzic czy da sie oplacic punktami
                if(checkForPointsPartialValue(order, paymentMethod, tryCombination)) break;
            }            
                //sprawdzam czy ta kombinacja rabatow jest lepsze od poprzedniej i jesli tak - zapisuje bieżące dane
                if(tryCombination.totalDiscount.compareTo(masterCombination.totalDiscount) > 0){
                    masterCombination.totalDiscount = tryCombination.totalDiscount;
                             
            }
        }
        
                    System.out.println("/nPo przejsciu petli pierwszej:/n");
                    orders.forEach(System.out::println);
                    paymentMethods.forEach(System.out::println);
                    System.out.println("Suma rabatow: " + tryCombination.totalDiscount.toString());
        
        // sprawdzam, czy oplacenie najpierw kartami, a potem punktami da lepszy efekt, jesli bedzie taki sam, poprzednie
        // ustawienie premiujace punkty nie zostanie nadpisane
        tryCombination.totalDiscount = BigInteger.ZERO;

        DataResetter resetter2 = resetData(ordersPath, methodsPath);
        orders = resetter2.orders;
        paymentMethods = resetter2.paymentMethods;
//        unsortedOrders = myMapper.readValue(ordersFile, new TypeReference<>(){});
//        unsortedPaymentMethods = myMapper.readValue(paymentMethodsFile, new TypeReference<>(){});
//        
//        orders = new ArrayList<>(unsortedOrders);
        orders.sort(Comparator.comparing(Order::getValue).reversed());
//        
//        ascendingOrders = new ArrayList<>(unsortedOrders);
//        ascendingOrders.sort(Comparator.comparing(Order::getValue));
//
//        paymentMethods = new ArrayList<>(unsortedPaymentMethods);
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
//        
//        paymentMethods = unsortedPaymentMethods;
//        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
        
        System.out.println("/nPrzed druga petla:/n");
                    orders.forEach(System.out::println);
                    paymentMethods.forEach(System.out::println);
                    System.out.println("Suma rabatow: " + tryCombination.totalDiscount.toString());
        
        for(Order order : orders){
            for(PaymentMethod paymentMethod : paymentMethods){
                if(checkForCards(order, paymentMethod, tryCombination)) continue;
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                if(checkForPointsFullValue(order, paymentMethod, tryCombination)) break;
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                // jesli nie da sie oplacic w calosci z rabatem to sprawdzic czy da sie oplacic punktami
                if(checkForPointsPartialValue(order, paymentMethod, tryCombination)) break;
            }            
                //sprawdzam czy ta kombinacja rabatow jest lepsze od poprzedniej i jesli tak - zapisuje bieżące dane
                if(tryCombination.totalDiscount.compareTo(masterCombination.totalDiscount) > 0){
                    masterCombination.totalDiscount = tryCombination.totalDiscount;
            }
        }        
                    System.out.println("/nPo przejsciu petli drugiej:/n");
                        orders.forEach(System.out::println);
                        paymentMethods.forEach(System.out::println);
                        System.out.println("Suma rabatow: " + tryCombination.totalDiscount.toString());
        
        // sprawdzam, czy oplacenie najpierw tańszych, a potem droższych zamówień dla lepszy wynik, jesli bedzie taki sam, poprzednie
        // ustawienie nie zostanie nadpisane
        tryCombination.totalDiscount = BigInteger.ZERO;
        
        DataResetter resetter3 = resetData(ordersPath, methodsPath);
        orders = resetter3.orders;
        paymentMethods = resetter3.paymentMethods;

//        unsortedOrders = myMapper.readValue(ordersFile, new TypeReference<>(){});
//        unsortedPaymentMethods = myMapper.readValue(paymentMethodsFile, new TypeReference<>(){});
//        
//        orders = new ArrayList<>(unsortedOrders);
        orders.sort(Comparator.comparing(Order::getValue));
//        
//        ascendingOrders = new ArrayList<>(orders);
//        ascendingOrders.sort(Comparator.comparing(Order::getValue));
//
//        paymentMethods = new ArrayList<>(unsortedPaymentMethods);
//        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
//        
//        paymentMethods = unsortedPaymentMethods;
        paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
        for(Order order : orders){           
            for(PaymentMethod paymentMethod : paymentMethods){
                if(checkForCards(order, paymentMethod, tryCombination)) continue; //
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                if(checkForPointsFullValue(order, paymentMethod, tryCombination)) break;
            }
            for(PaymentMethod paymentMethod : paymentMethods){
                // jesli nie da sie oplacic w calosci z rabatem to sprawdzic czy da sie oplacic punktami, ale tutaj trzeba dac najwyzsze zamowienie, jakie sie kwalifikuje!!!
                if(checkForPointsPartialValue(order, paymentMethod, tryCombination)) break;
            }            
                //sprawdzam czy ta kombinacja rabatow jest lepsze od poprzedniej i jesli tak - zapisuje bieżące dane
                if(tryCombination.totalDiscount.compareTo(masterCombination.totalDiscount) > 0){
                    masterCombination.totalDiscount = tryCombination.totalDiscount;                          
            }
        }        
                    System.out.println("/nPo przejsciu petli trzeciej:/n");
                        orders.forEach(System.out::println);
                        paymentMethods.forEach(System.out::println);
                        System.out.println("Suma rabatow: " + tryCombination.totalDiscount.toString());
        
        
        // dodać pętle w innych kolejnosciach

        //oplacenie pozostalych niezrabatowanych kwot zamowien
        for(Order order : orders){
            for(PaymentMethod paymentMethod : paymentMethods){
            if(!order.value.equals(BigInteger.ZERO)){
                    if(paymentMethod.limit.compareTo(order.value) > 0){
                        paymentMethod.limit = paymentMethod.limit.subtract(order.value);
                        order.value = BigInteger.ZERO;
                        break;
                    }
                    if(paymentMethod.limit.compareTo(order.value) < 0){
                        order.value.subtract(paymentMethod.limit);
                        paymentMethod.limit = BigInteger.ZERO;
                        continue;
                    }
                }
            }
        }
        
        System.out.println("/nPo oplaceniu wszystkiego:/n");
        orders.forEach(System.out::println);
        paymentMethods.forEach(System.out::println);
        System.out.println("Suma rabatow: " + masterCombination.totalDiscount.toString());
        
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
