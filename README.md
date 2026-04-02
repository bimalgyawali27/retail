# Retail Rewards API

Spring Boot REST API for calculating customer reward points based on purchase history.

## How points work

- $0 - $50: no points
- $50 - $100: 1 point per dollar
- over $100: 2 points per dollar

example: $120 purchase = 2x$20 + 1x$50 = 90 points

## Run

```bash
mvn spring-boot:run
```

## Endpoints

```
GET /api/v1/rewards                     - all customers monthly + total points
GET /api/v1/rewards/{customerId}        - single customer (C001 to C005)
GET /api/v1/rewards/transactions        - raw transactions
GET /api/v1/rewards/calculate?amount=   - points for a given amount
```

## Test

```bash
mvn test
```
