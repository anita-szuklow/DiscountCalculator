## DiscountCalculator – optimization application for balancing payments with discounts

### Overview

This application is designed to optimally assign discounts and payment methods (cards, points) 
to orders in order to maximize total savings. It is written in Java using the Strategy pattern.

### Prerequisites

- Java 21+
- Maven 3.9+

---

### Project Structure

```
DiscountCalculator/
├── pom.xml
├── nbactions.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── mycompany/
│   │               └── discountcalculator/
│   │                   ├── Combination.java
│   │                   ├── DataResetter.java
│   │                   ├── DiscountCalculatorApplication.java
│   │                   ├── Order.java
│   │                   ├── PaymentMethod.java
│   │                   └── strategies/
│   │                       ├── Strategy.java
│   │                       ├── StrategyBestDiscountEfficiency.java
│   │                       ├── StrategyBiggestDiscountValue.java
│   │                       ├── StrategyCardsFirstOrdersDescendingByMethodsAllowedValDes.java
│   │                       ├── StrategyCardsFirstOrdersDescendingMethodsDescending.java
│   │                       ├── StrategyDiscountMethodsRatio.java
│   │                       ├── StrategyPointsFirstOrdersAscendingByMethodsAllowedValDes.java
│   │                       ├── StrategyPointsFirstOrdersAscendingByMethodsAllowValuesDescending.java
│   │                       ├── StrategyPointsFirstOrdersAscendingMethodsDescending.java
│   │                       └── StrategyPointsFirstOrdersDescendingMethodsDescending.java
│   └── test/
│       └── java/
│           └── com/
│               └── mycompany/
│                   └── discountcalculator/
│                       └── DiscountTest.java
```

---

### Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/anita-szuklow/DiscountCalculator
cd DiscountCalculator
```

2. Place the JSON files (`orders.json` and `paymentmethods.json`) in any accessible directory.

---

### Building and Running

> The first argument is the absolute path to `orders.json`,  
> the second argument is the absolute path to `paymentmethods.json`.

```bash
mvn clean package
mvn exec:java -Dexec.mainClass="com.mycompany.discountcalculator.DiscountCalculatorApplication" \
    -Dexec.args="path/to/orders.json path/to/paymentmethods.json"
```

The application will:
1. Parse the JSON files to obtain a set of orders and applicable payment methods.
2. Try 9 different strategies for assigning methods to orders and calculate total discounts.
3. Choose the strategy that provides the highest `totalDiscount`. If several strategies produce equal results, the first one encountered is retained (strategies that prioritize `PUNKTY` are evaluated first).
4. Apply the selected strategy again on fresh data to complete any remaining payments.
5. Print a summary of how much was paid with each method.

---

### Input File Format

**orders.json**
```json
[
  {"id": "ORDER1", "value": "100.00", "promotions": ["mZysk"]},
  {"id": "ORDER2", "value": "200.00", "promotions": ["BosBankrut"]},
  {"id": "ORDER3", "value": "150.00", "promotions": ["mZysk", "BosBankrut"]},
  {"id": "ORDER4", "value": "50.00"}
]
```

**paymentmethods.json**
```json
[
  {"id": "PUNKTY", "discount": "15", "limit": "100.00"},
  {"id": "mZysk", "discount": "10", "limit": "180.00"},
  {"id": "BosBankrut", "discount": "5", "limit": "200.00"}
]
```

---

### Strategy Pattern

The interface `AssignmentStrategy` defines the method:

```java
void apply(List<Order> orders, List<PaymentMethod> methods, Combination result);
```

Implemented strategies (in the `strategy/` package):

- `StrategyBestDiscountEfficiency` – greedy by discount efficiency (discount ÷ order amount)
- `StrategyBiggestDiscountValue` – greedy by absolute discount value
- `StrategyCardsFirstOrdersDescendingMethodsDescending`
- `StrategyCardsFirstOrdersDescendingByMethodsAllowedValDes`
- `StrategyDiscountMethodsRatio`
- `StrategyPointsFirstOrdersAscendingMethodsDescending`
- `StrategyPointsFirstOrdersAscendingByMethodsAllowedValDes`
- `StrategyPointsFirstOrdersAscendingMethodsDescendingByMethodsAllowedValDes`
- `StrategyPointsFirstOrdersDescendingMethodsDescending`

The application tests each of these strategies, picks the best, and reapplies it to finalize unpaid balances.

---

### Testing

Includes basic test coverage for:

- Full value point payments
- Partial point payments
- Card-based payments
- Internal combination state logic

Run tests with:

```bash
mvn test
```

---

### Extensibility

You can add new strategies to refine the optimization process and potentially improve discount efficiency.