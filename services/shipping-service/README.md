# Shipping Service

## Overview

The Shipping Service manages shipment creation, tracking, and status updates. It generates tracking numbers, calculates delivery estimates, and publishes shipment events for order fulfillment workflows.

## Features

- **Shipment Creation**: Create shipments from order details
- **Tracking Number Generation**: Auto-generated realistic tracking numbers
- **Status Management**: Complete shipment lifecycle tracking
- **Event Publishing**: Kafka-based shipment event notifications
- **Delivery Estimates**: Automatic delivery date calculation
- **RESTful API**: Complete shipment management APIs

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Database**: PostgreSQL with Liquibase migrations
- **Messaging**: Apache Kafka for event streaming
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Apache Kafka

## Configuration

Environment variables:
- `DB_HOST`: Database host (default: localhost)
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name (default: shipping_db)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka servers

## Running the Service

### Local Development

```bash
# Build the service
mvn clean install

# Run the service
mvn spring-boot:run
```

### Docker

```bash
mvn clean package
docker build -t supplyboost/shipping-service:latest .
docker run -d -p 8087:8087 \
  -e DB_HOST=postgres \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  supplyboost/shipping-service:latest
```

## API Endpoints

### Create Shipment
```http
POST /api/v1/shipments
{
  "orderId": 1,
  "orderNumber": "ORD-123",
  "userId": 1,
  "recipientName": "John Doe",
  "recipientEmail": "john@example.com",
  "addressLine1": "123 Main St",
  "city": "San Francisco",
  "state": "CA",
  "postalCode": "94102",
  "country": "USA"
}
```

### Track Shipment
```http
GET /api/v1/shipments/tracking/{trackingNumber}
```

### Update Status
```http
PUT /api/v1/shipments/{shipmentId}/status?status=SHIPPED
```

## Shipment Status Flow

```
PENDING → PROCESSING → SHIPPED → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
```

## Events Published

- `shipment.created`: When shipment is created
- `shipment.status.updated`: When status changes

## API Documentation

http://localhost:8087/swagger-ui.html

## License

Apache License 2.0
