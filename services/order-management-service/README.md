# Order Management Service

## Overview

The Order Management Service handles the complete order lifecycle from creation through fulfillment. It orchestrates the order saga pattern, coordinating with shopping cart, payment, shipping, and notification services to ensure consistent order processing.

## Features

- **Order Creation**: Create orders from shopping cart
- **Order State Machine**: Track order status through the lifecycle
- **Saga Orchestration**: Coordinate distributed transactions
- **Event Publishing**: Kafka-based event notifications
- **Order Tracking**: Complete order history and status updates
- **RESTful API**: Complete CRUD operations for orders

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Database**: PostgreSQL with Liquibase migrations
- **Messaging**: Apache Kafka for event streaming
- **State Management**: Spring State Machine
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Apache Kafka
- Shopping Cart Service running

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_management_db
  kafka:
    bootstrap-servers: localhost:9092

services:
  shopping-cart:
    url: http://localhost:8083
```

Environment variables:
- `DB_HOST`: Database host (default: localhost)
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name (default: order_management_db)
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka servers
- `SHOPPING_CART_URL`: Shopping Cart Service URL

## Running the Service

### Local Development

```bash
# Start PostgreSQL and Kafka
docker-compose up -d postgres kafka

# Build the service
mvn clean install

# Run the service
mvn spring-boot:run
```

### Docker

```bash
# Build Docker image
mvn clean package
docker build -t supplyboost/order-management-service:latest .

# Run container
docker run -d -p 8084:8084 \
  -e DB_HOST=postgres \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SHOPPING_CART_URL=http://shopping-cart-service:8083 \
  supplyboost/order-management-service:latest
```

## API Endpoints

### Create Order
```http
POST /api/v1/orders
Content-Type: application/json

{
  "userId": 1,
  "cartId": "cart-123",
  "shippingAddress": {
    "line1": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "postalCode": "94102",
    "country": "USA"
  },
  "billingAddress": { ... },
  "customerEmail": "customer@example.com",
  "customerName": "John Doe"
}
```

### Get Order
```http
GET /api/v1/orders/{orderId}
```

### Get User Orders
```http
GET /api/v1/orders/user/{userId}
```

### Update Order Status
```http
PUT /api/v1/orders/{orderId}/status?status=PAYMENT_CONFIRMED
```

### Cancel Order
```http
DELETE /api/v1/orders/{orderId}
```

## API Documentation

Once the service is running, access the Swagger UI at:
```
http://localhost:8084/swagger-ui.html
```

## Order Status Flow

```
CREATED
  ↓
PAYMENT_PENDING
  ↓
PAYMENT_CONFIRMED / PAYMENT_FAILED
  ↓
INVENTORY_RESERVED / INVENTORY_RESERVATION_FAILED
  ↓
READY_TO_SHIP
  ↓
SHIPPED
  ↓
DELIVERED / CANCELLED / REFUNDED
```

## Database Schema

### Orders Table
- Order metadata (id, order_number, user_id, status)
- Shipping and billing addresses
- Payment information
- Shipment tracking
- Audit timestamps

### Order Items Table
- Product details (id, sku, name)
- Pricing (unit_price, quantity, subtotal)
- Reference to order

## Event Publishing

### Order Created Event
Published to: `order.created` topic
```json
{
  "orderId": 1,
  "orderNumber": "ORD-123456",
  "userId": 1,
  "items": [...],
  "totalAmount": 99.99,
  "customerEmail": "customer@example.com"
}
```

### Order Status Changed Event
Published to: `order.status.changed` topic
```json
{
  "orderId": 1,
  "orderNumber": "ORD-123456",
  "oldStatus": "CREATED",
  "newStatus": "PAYMENT_CONFIRMED",
  "changedAt": "2025-11-22T10:30:00"
}
```

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Monitoring

Health check endpoint:
```
GET /actuator/health
```

Metrics (Prometheus):
```
GET /actuator/prometheus
```

## Error Handling

- `404 Not Found`: Order or cart not found
- `400 Bad Request`: Invalid input or empty cart
- `500 Internal Server Error`: Unexpected errors

## Security

- Currently configured with permissive access for development
- Integration with API Gateway for authentication in production
- CSRF disabled for stateless REST API

## Future Enhancements

- Order modification after placement
- Partial shipments
- Returns and refunds workflow
- Order splitting
- Advanced saga compensation logic

## License

Apache License 2.0
