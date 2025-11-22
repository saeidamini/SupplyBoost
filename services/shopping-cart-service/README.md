# Shopping Cart Service

## Overview

The Shopping Cart Service manages user shopping carts with Redis-based storage for fast, session-based cart operations. It integrates with the Product Catalog Service to validate products and check stock availability.

## Features

- **Redis-based Storage**: Fast, in-memory cart storage with TTL support
- **Cart Management**: Add, update, remove items from cart
- **Price Calculation**: Automatic subtotal and total calculations
- **Stock Validation**: Real-time inventory checks via Product Catalog Service
- **Session Support**: Cart persistence across sessions (7-day TTL)
- **RESTful API**: Complete CRUD operations for cart management

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Cache**: Redis (with Spring Data Redis)
- **Documentation**: OpenAPI/Swagger
- **Serialization**: Jackson with Java Time support
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- Redis 6+
- Product Catalog Service running (for product validation)

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  redis:
    host: localhost
    port: 6379

services:
  product-catalog:
    url: http://localhost:8082
```

Environment variables:
- `REDIS_HOST`: Redis server host (default: localhost)
- `REDIS_PORT`: Redis server port (default: 6379)
- `PRODUCT_CATALOG_URL`: Product Catalog Service URL

## Running the Service

### Local Development

```bash
# Start Redis (via Docker)
docker run -d -p 6379:6379 redis:7-alpine

# Build the service
mvn clean install

# Run the service
mvn spring-boot:run
```

### Docker

```bash
# Build Docker image
mvn clean package
docker build -t supplyboost/shopping-cart-service:latest .

# Run container
docker run -d -p 8083:8083 \
  -e REDIS_HOST=redis \
  -e PRODUCT_CATALOG_URL=http://product-catalog-service:8082 \
  supplyboost/shopping-cart-service:latest
```

## API Endpoints

### Get Cart
```http
GET /api/v1/cart/{cartId}
```

### Add Item to Cart
```http
POST /api/v1/cart/{cartId}/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

### Update Cart Item
```http
PUT /api/v1/cart/{cartId}/items/{productId}
Content-Type: application/json

{
  "quantity": 3
}
```

### Remove Item from Cart
```http
DELETE /api/v1/cart/{cartId}/items/{productId}
```

### Clear Cart
```http
DELETE /api/v1/cart/{cartId}
```

## API Documentation

Once the service is running, access the Swagger UI at:
```
http://localhost:8083/swagger-ui.html
```

OpenAPI specification:
```
http://localhost:8083/api-docs
```

## Architecture

### Data Model

**ShoppingCart**
- `id`: Cart identifier (userId or sessionId)
- `userId`: User ID (if authenticated)
- `items`: List of cart items
- `totalAmount`: Total cart value
- `ttl`: Time-to-live (7 days)

**CartItem**
- `productId`: Product identifier
- `productName`: Product name
- `productSku`: Product SKU
- `unitPrice`: Price per unit
- `quantity`: Item quantity
- `subtotal`: Calculated item total

### External Dependencies

- **Product Catalog Service**: Product validation and pricing
- **Redis**: Cart storage and session management

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

- `404 Not Found`: Cart or product not found
- `400 Bad Request`: Invalid input or insufficient stock
- `500 Internal Server Error`: Unexpected errors

## Security

- Currently configured with permissive access for development
- Integration with API Gateway for authentication in production
- CSRF disabled for stateless REST API

## Future Enhancements

- Cart merging for authenticated users
- Promotional code support
- Cart abandonment notifications
- Product availability notifications
- Multi-currency support

## License

Apache License 2.0
