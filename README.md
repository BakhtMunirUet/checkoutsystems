# Supermarket Checkout System

## Overview
This project implements a **simplified supermarket checkout system**. It calculates the total price for any number and combination of available items and automatically applies **weekly offers** to eligible items.

The system is designed to be flexible, allowing easy addition of new items and offers.

## Features
- **Flexible Checkout**: Accepts any number and combination of items, in any order.
- **Weekly Offers**: Automatically applies discounts when items are bought in specified combinations.
    - Example:
        - One apple costs **0.30€**
        - Two apples are offered for **0.45€**
- **Automatic Pricing**: Calculates the total including discounts without manual intervention.

## How It Works
1. The user provides a list of items for checkout.
2. The system checks for applicable weekly offers.
3. The total price is calculated automatically, including any discounts.

## Example
```text
Items:
- 3 apples
- 1 banana

Pricing:
- Apples: 2 for 0.45€ + 1 for 0.30€ = 0.75€
- Banana: 1 for 0.50€
Total: 1.25€
```

## Installation
1. Clone the repository:

```bash
git clone https://github.com/yourusername/supermarket-checkout.git
```

## Navigate to the project directory:
2. Clone the repository:
```bash
cd supermarket-checkout
```

## Navigate to the project directory:
3. Build the project using Maven or Gradle (if Java/Spring Boot):
```bash
mvn clean install
```

## Usage
### Run the application
Using Maven:
```bash
mvn spring-boot:run
```

### How to Run Tests
Using Maven:

```bash
mvn test
```

### Checkout API Example

### Request
POST `http://localhost:8080/api/v1/checkout`

```json
{
  "items": [
    { "itemId": 6, "name": "Apple", "quantity": 3 },
    { "itemId": 7, "name": "Banana", "quantity": 1 }
  ]
}
```

### Response

```json
{
    "id": 10,
    "totalPrice": 0.95,
    "totalDiscount": 0.15,
    "items": [
        {
            "id": 19,
            "itemId": 6,
            "itemName": "Apple",
            "quantity": 3,
            "unitPrice": 0.30,
            "totalPrice": 0.75,
            "originalTotalPrice": 0.90,
            "discountApplied": 0.15,
            "timesOfferApplied": 1
        },
        {
            "id": 20,
            "itemId": 7,
            "itemName": "Banana",
            "quantity": 1,
            "unitPrice": 0.20,
            "totalPrice": 0.20,
            "originalTotalPrice": 0.20,
            "discountApplied": 0,
            "timesOfferApplied": 0
        }
    ]
}

```

### Checkout API Response Fields

- `id` → Checkout transaction ID
- `totalPrice` → Final price after applying all discounts
- `totalDiscount` → Total savings from offers
- `items` → List of items in the checkout, each with:
    - `id` → Item record ID in the checkout
    - `itemId` → Original item ID
    - `itemName` → Name of the item
    - `quantity` → Number of units purchased
    - `unitPrice` → Price per single unit
    - `totalPrice` → Price after discount for this item
    - `originalTotalPrice` → Price without any discount
    - `discountApplied` → Discount applied to this item
    - `timesOfferApplied` → How many times the offer was used

