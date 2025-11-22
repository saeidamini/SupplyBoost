# Payment Service

## Overview

The Payment Service handles all payment processing for orders. It supports both Stripe integration for production use and a mock mode for development and testing. The service manages payment intents, confirmations, and refunds while publishing events for order processing.

## Features

- **Dual Mode Operation**: Stripe integration and mock mode
- **Payment Processing**: Create payment intents, confirm payments
- **Refund Management**: Full and partial refund support
- **Stripe Integration**: Complete Stripe SDK integration
- **Event Publishing**: Kafka-based payment event notifications
- **RESTful API**: Complete payment management APIs

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Database**: PostgreSQL with Liquibase migrations
- **Payment Provider**: Stripe SDK 24.3.0
- **Messaging**: Apache Kafka for event streaming
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Apache Kafka
- Stripe Account (optional, for production mode)

## Configuration

### Payment Modes

**Mock Mode** (default for development):
```yaml
payment:
  mode: mock
```

**Stripe Mode** (production):
```yaml
payment:
  mode: stripe
stripe:
  api:
    key: sk_test_your_stripe_key
```

### Environment Variables
- `DB_HOST`: Database host (default: localhost)
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name (default: payment_db)
- `PAYMENT_MODE`: Payment mode (mock or stripe)
- `STRIPE_API_KEY`: Stripe API key (required for stripe mode)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka servers

## Running the Service

### Local Development (Mock Mode)

```bash
# Start PostgreSQL and Kafka
docker-compose up -d postgres kafka

# Build the service
mvn clean install

# Run in mock mode (default)
mvn spring-boot:run
```

### Production Mode (Stripe)

```bash
# Set environment variables
export PAYMENT_MODE=stripe
export STRIPE_API_KEY=sk_live_your_key

# Run the service
mvn spring-boot:run
```

### Docker

```bash
# Build Docker image
mvn clean package
docker build -t supplyboost/payment-service:latest .

# Run container
docker run -d -p 8086:8086 \
  -e DB_HOST=postgres \
  -e PAYMENT_MODE=stripe \
  -e STRIPE_API_KEY=sk_live_your_key \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  supplyboost/payment-service:latest
```

## API Endpoints

### Create Payment
```http
POST /api/v1/payments
Content-Type: application/json

{
  "orderId": 1,
  "orderNumber": "ORD-123",
  "userId": 1,
  "amount": 99.99,
  "currency": "USD",
  "customerEmail": "customer@example.com",
  "customerName": "John Doe",
  "description": "Order payment"
}
```

### Confirm Payment
```http
POST /api/v1/payments/{paymentId}/confirm
Content-Type: application/json

{
  "paymentMethod": "card"
}
```

### Get Payment
```http
GET /api/v1/payments/{paymentId}
```

### Get Payment by Order
```http
GET /api/v1/payments/order/{orderId}
```

### Refund Payment
```http
POST /api/v1/payments/{paymentId}/refund
Content-Type: application/json

{
  "amount": 99.99,
  "reason": "Customer requested refund"
}
```

## API Documentation

Once the service is running, access the Swagger UI at:
```
http://localhost:8086/swagger-ui.html
```

## Payment Flow

### Mock Mode
1. Create payment → Status: PENDING
2. Confirm payment → Auto-approved → Status: SUCCEEDED
3. Event published to Kafka

### Stripe Mode
1. Create payment → Creates Stripe PaymentIntent → Status: PENDING
2. Confirm payment → Stripe processes → Status: SUCCEEDED/FAILED
3. Event published to Kafka

## Database Schema

### Payments Table
- Payment metadata (id, payment_id, order_id, amount)
- Payment status tracking
- Stripe-specific fields (payment_intent_id, client_secret)
- Customer information
- Refund details
- Audit timestamps

## Event Publishing

### Payment Processed Event
Published to: `payment.processed` topic
```json
{
  "paymentId": 1,
  "paymentNumber": "PAY-123",
  "orderId": 1,
  "orderNumber": "ORD-123",
  "userId": 1,
  "amount": 99.99,
  "currency": "USD",
  "status": "SUCCEEDED",
  "paymentMethod": "card",
  "eventTime": "2025-11-22T10:30:00"
}
```

## Payment Status Flow

```
PENDING → PROCESSING → SUCCEEDED
                     ↓
                   FAILED

SUCCEEDED → REFUNDED
```

## Testing

### Mock Mode Testing
```bash
# No Stripe credentials needed
# Payments auto-approve
curl -X POST http://localhost:8086/api/v1/payments/PAY-123/confirm \
  -H "Content-Type: application/json" \
  -d '{"paymentMethod": "test_card"}'
```

### Stripe Test Mode
Use Stripe test cards:
- Success: 4242 4242 4242 4242
- Decline: 4000 0000 0000 0002

## Monitoring

Health check:
```
GET /actuator/health
```

Metrics (Prometheus):
```
GET /actuator/prometheus
```

## Error Handling

- `404 Not Found`: Payment not found
- `400 Bad Request`: Invalid request or payment not refundable
- `500 Internal Server Error`: Payment processing errors

## Security

- Stripe API keys must be kept secure
- Never commit API keys to source control
- Use environment variables for sensitive configuration
- CSRF disabled for stateless REST API

## Stripe Webhooks

For production, configure webhooks at:
```
POST /api/v1/webhooks/stripe
```

Handles events:
- payment_intent.succeeded
- payment_intent.payment_failed
- charge.refunded

## Future Enhancements

- Additional payment providers (PayPal, Square)
- Subscription and recurring payments
- Payment method storage
- 3D Secure support
- Multi-currency support
- Payment analytics dashboard

## License

Apache License 2.0
