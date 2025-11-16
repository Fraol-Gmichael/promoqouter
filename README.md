# PromoQouter — Promotion & Cart Quoting Service

PromoQouter is a Spring Boot service that manages product promotions and provides cart quoting/confirmation APIs. It
supports multiple promotion types (fixed amount discount, percent off, buy X get Y, tiered bulk discounts, promo codes),
customer segments, targets (product/category/quantity), and applies the best matching promotions to a cart.

The service exposes REST endpoints to manage products and promotions, and to quote or confirm a cart. It uses an
in‑memory H2 database with seed data for quick local testing.

## Key Features

- Product CRUD (name, price, stock, category)
- Promotion CRUD with rich options:
    - Types: FIXED_DISCOUNT, PERCENT_OFF, BUY_X_GET_Y, PROMO_CODE, TIERED_BULK_DISCOUNT (enums available for future
      types)
    - Targets: PRODUCT, CATEGORY, QTY
    - Customer segments: ALL, REGULAR, GOLD, etc.
    - Budgets, max usage per user, priority, active window (start/end dates)
    - Tiered/bulk discount configuration
    - Promo code based discounts
- Cart quote and confirm flows that:
    - Validate products and quantities, check stock
    - Retrieve applicable promotions for items/segment/promo codes
    - Apply promotions via a pluggable Promotion Engine per type
    - Return detailed per-product discount information and cart totals
- Unified `ApiResponse<T>` response envelope (success, timestamp, internalCode, message, data, etc.)
- H2 console enabled for quick inspection

## Tech Stack

- Java 25
- Spring Boot 3.5.x (Web, Validation, Data JPA)
- H2 (in‑memory DB for local)
- MapStruct (mappers), Lombok
- Maven

## Getting Started

### Prerequisites

- Java 25 installed and active (`java -version` should show 25)
- Maven 3.9+ (or use the provided wrapper `./mvnw`)

### Run Locally

- Using Maven wrapper (recommended):
  ```bash
  ./mvnw spring-boot:run
  ```
- Or build and run the jar:
  ```bash
  ./mvnw clean package
  java -jar target/promoqouter-0.0.1-SNAPSHOT.jar
  ```

Service will start on http://localhost:8080

### H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

H2 console is enabled via `src/main/resources/application.yml`.

## Seed Data (on startup)

A `DataLoader` seeds products and promotions for easy testing:

Products (IDs):

- Ultra Laptop 14 — 11111111-1111-1111-1111-111111111111 (ELECTRONICS, 1299.99)
- Noise-Canceling Headphones — 22222222-2222-2222-2222-222222222222 (ELECTRONICS, 199.99)
- Classic Denim Jacket — 33333333-3333-3333-3333-333333333333 (FASHION, 89.90)
- Organic Apples (1kg) — 44444444-4444-4444-4444-444444444444 (GROCERIES, 3.49)
- Sparkling Water (12-pack) — 55555555-5555-5555-5555-555555555555 (BEVERAGES, 7.99)
- Premium Coffee Beans 1kg — 66666666-6666-6666-6666-666666666666 (BEVERAGES, 19.95)

Sample promotions:

- Autumn Fashion -10% (PERCENT_OFF, target CATEGORY=FASHION)
- Laptop $50 Off (FIXED_DISCOUNT, target PRODUCT=Laptop, maxUsagePerUser=1)
- Groceries B2G1 (BUY_X_GET_Y, target CATEGORY=GROCERIES, buy 2 get 1)
- SUMMER20 (PROMO_CODE 20% off, code: `SUMMER20`)
- Beverages Bulk Save (TIERED_BULK_DISCOUNT: 6–10 => 5%, 11+ => 10%)

## API Overview

All endpoints return `ApiResponse<T>`:

```json
{
  "success": true,
  "internalCode": "200",
  "message": "OK",
  "timestamp": "2025-11-16T22:03:00",
  "data": {}
}
```

Base URL: `http://localhost:8080`

### Products

- POST `/products` — Create product
- PUT `/products/{id}` — Update product
- GET `/products/{id}` — Get product
- DELETE `/products/{id}` — Delete product
- GET `/products` — List products

Sample create:

```bash
curl -s -X POST http://localhost:8080/products \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "USB-C Cable",
    "category": "ELECTRONICS",
    "price": 9.99,
    "stock": 100
  }'
```

Payload types: see `CreateProductRequestDto`, `UpdateProductRequestDto`, `Product`.

### Promotions

- POST `/promotions` — Create promotion
- PUT `/promotions/{id}` — Update promotion
- GET `/promotions/{id}` — Get promotion
- DELETE `/promotions/{id}` — Delete promotion
- GET `/promotions` — List promotions
- GET `/promotions/get-by-name/{name}` — Find by (case‑insensitive) name

Sample create (percent off by category):

```bash
curl -s -X POST http://localhost:8080/promotions \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Black Friday 15% Fashion",
    "status": "ACTIVE",
    "type": "PERCENT_OFF",
    "percentOffAmount": 15,
    "target": "CATEGORY",
    "targetCategory": "FASHION",
    "priority": 100
  }'
```

Promotion enums:

- `Promotion.Type`: FIXED_DISCOUNT, BUY_X_GET_Y, FREE_SHIPPING, POINTS_REWARD, PERCENT_OFF, PROMO_CODE,
  TIERED_BULK_DISCOUNT
- `Promotion.Target`: PRODUCT, CATEGORY, QTY
- `Promotion.Status`: ACTIVE, INACTIVE

DTOs: `CreatePromotionRequestDto`, `UpdatePromotionRequestDto`, `Promotion` (service model).

### Cart

- POST `/cart/quote` — Calculates discounts and totals without affecting inventory
- POST `/cart/confirm` — Recalculates and finalizes (e.g., to reserve/confirm); same request body as quote

Request body (`CartRequestDto`):

```json
{
  "items": [
    {
      "productId": "33333333-3333-3333-3333-333333333333",
      "qty": 2
    },
    {
      "productId": "55555555-5555-5555-5555-555555555555",
      "qty": 12
    }
  ],
  "customerSegment": "ALL",
  "promoCodes": [
    "SUMMER20"
  ]
}
```

Sample quote:

```bash
curl -s -X POST http://localhost:8080/cart/quote \
  -H 'Content-Type: application/json' \
  -d '{
    "items": [
      { "productId": "33333333-3333-3333-3333-333333333333", "qty": 2 },
      { "productId": "55555555-5555-5555-5555-555555555555", "qty": 12 }
    ],
    "customerSegment": "ALL",
    "promoCodes": ["SUMMER20"]
  }'
```

Response (`Cart`): includes per‑product discount info and totals. Example fields you will see:

```json
{
  "success": true,
  "data": {
    "items": [],
    "productDiscountInfos": {
      "33333333-3333-3333-3333-333333333333": {
        "product": {
          "id": "33333333-3333-3333-3333-333333333333",
          "name": "Classic Denim Jacket",
          "price": 89.9
        },
        "cartItem": {
          "productId": "33333333-3333-3333-3333-333333333333",
          "qty": 2
        },
        "discounts": [],
        "lineTotal": 161.82
      }
    },
    "totalPrice": 200.70,
    "totalDiscount": 25.50,
    "finalPrice": 175.20
  }
}
```

Notes:

- Validation enforces required fields (`productId`, `qty` positive, at least one item, etc.).
- Available promotions are filtered by product(s), category, status, active window, customer segment, and optional promo
  codes.
- For tiered bulk discounts, tiers are validated to be continuous and non‑overlapping.

## Build, Test, Lint

- Build: `./mvnw clean package`
- Run unit tests: `./mvnw test`

Generated sources (MapStruct) are written under `target/generated-sources/annotations`.

## Project Structure

```
/ (project root)
├─ pom.xml
├─ src
│  ├─ main
│  │  ├─ java/com/fraolgmichael/promoqouter
│  │  │  ├─ PromoqouterApplication.java
│  │  │  ├─ DataLoader.java                     # Seeds products & promotions
│  │  │  ├─ common/...
│  │  │  ├─ product/...
│  │  │  └─ promotion/...
│  │  └─ resources/
│  │     └─ application.yml
│  └─ test/java/com/fraolgmichael/promoqouter  # Unit tests
└─ README.md
```

## Configuration

See `application.yml` for app name and H2 console settings. JPA/H2 use Spring Boot defaults for an in‑memory database.

## Design Notes

- Promotion application is handled by type‑specific engines (`PromotionEngine` and implementations like
  `PromotionEnginePercentOFFImpl`, `PromotionEngineBuyXGetYImpl`, `PromotionEngineFixedDiscountImpl`,
  `PromotionEngineTieredBulkDiscountImpl`).
- `AvailablePromotionsFilterHelper` gathers applicable promotions for the current cart context (items, categories,
  customer segment, promo codes, date window, status), and the service applies them to compute totals.
- `ApiResponse<T>` standardizes responses across controllers (products, promotions, cart).

## License

This project is provided as‑is for demonstration/learning purposes. Update licensing information as needed.
