package com.mycompany.discountcalculator;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.math.BigInteger;
import java.util.ArrayList;

public class Order {
    public String id;
    public BigInteger value;
//    public BigInteger initialValue;
    public List<String> promotions = new ArrayList(List.of("PUNKTY"));
    
    public Order(){
    }
    
    public Order(Order newOrder){
        this.id = newOrder.id;
        this.value = newOrder.value;
        this.promotions = newOrder.promotions;
    }
    
    @JsonProperty("value")
    public void setValue(BigDecimal valueFromJson){
        this.value = valueFromJson.multiply(BigDecimal.valueOf(100)).toBigInteger();
    }
    
    @JsonProperty("promotions")
    public void setPromotions(List<String> promotionsFromJson){
        if(promotionsFromJson !=null){
            this.promotions.addAll(promotionsFromJson);
        }
    }
    
    public BigInteger getValue(){
        return value;
    }
    
    @Override
    public String toString(){
        return "Id: '%s', value: '%s', promotions: '%s'".formatted(id, value, promotions);
    }
    
//    public void setInitial(){
//        this.value = initialValue;
//    }
//    public void reset(){
//        this.initialValue = value;
//    }
    
}
