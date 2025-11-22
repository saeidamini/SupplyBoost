# Identity Service

User authentication and authorization service for SupplyBoost.

## Features

- User registration with validation
- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- RESTful API endpoints
- OpenAPI/Swagger documentation
- Database migrations with Liquibase
- Prometheus metrics
- Health checks

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- JWT (JSON Web Tokens)
- Lombok
- Swagger/OpenAPI

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (or use Docker Compose from infrastructure/)
- Kafka (optional, for event publishing)

## Quick Start

### Using Docker Compose

```bash
# Start infrastructure services
cd ../../infrastructure/docker-compose
docker-compose up -d postgres

# Return to identity service
cd ../../services/identity-service

# Build and run
mvn spring-boot:run
```

### Standalone

```bash
# Build
mvn clean package

# Run
java -jar target/identity-service-0.1.0-SNAPSHOT.jar
```

## Configuration

Configure via `application.yml` or environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/identity_db
    username: supplyboost
    password: supplyboost_dev_password

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours in milliseconds

server:
  port: 8081
```

### Environment Variables

- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (MUST change in production)
- `SERVER_PORT` - Server port (default: 8081)

## API Endpoints

### Authentication

#### Register User
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890"
}
```

#### Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "SecurePass123!"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com",
    "username": "johndoe",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["ROLE_USER"]
  }
}
```

### User Management

#### Get User by ID
```bash
GET /api/v1/users/{userId}
Authorization: Bearer {token}
```

#### Get User by Username
```bash
GET /api/v1/users/username/{username}
Authorization: Bearer {token}
```

## API Documentation

Once the service is running:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs

## Health & Metrics

- **Health Check**: http://localhost:8081/actuator/health
- **Prometheus Metrics**: http://localhost:8081/actuator/prometheus
- **Info**: http://localhost:8081/actuator/info

## Database Schema

### Tables

#### users
- `id` (UUID, PK)
- `email` (VARCHAR, UNIQUE)
- `username` (VARCHAR, UNIQUE)
- `password` (VARCHAR)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `phone_number` (VARCHAR)
- `enabled` (BOOLEAN)
- `email_verified` (BOOLEAN)
- `account_non_locked` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `last_login_at` (TIMESTAMP)

#### roles
- `id` (BIGINT, PK)
- `name` (VARCHAR, UNIQUE)
- `description` (VARCHAR)

#### user_roles (join table)
- `user_id` (UUID, FK)
- `role_id` (BIGINT, FK)

### Default Roles

- `ROLE_USER` - Standard user
- `ROLE_ADMIN` - Administrator
- `ROLE_CUSTOMER` - Customer
- `ROLE_VENDOR` - Vendor

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with coverage
mvn clean test jacoco:report
```

## Security Considerations

### Development
- Default JWT secret is provided for development
- CSRF protection is disabled for stateless API
- Passwords are encrypted with BCrypt

### Production
- **MUST** change JWT secret
- **MUST** use HTTPS/TLS
- **MUST** use strong database credentials
- Consider implementing:
  - Rate limiting
  - Account lockout after failed attempts
  - Email verification
  - Two-factor authentication (2FA)
  - Refresh tokens
  - Token blacklisting

## Docker Build

```bash
# Build JAR
mvn clean package

# Build Docker image
docker build -t supplyboost/identity-service:latest .

# Run container
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/identity_db \
  -e SPRING_DATASOURCE_USERNAME=supplyboost \
  -e SPRING_DATASOURCE_PASSWORD=supplyboost_dev_password \
  supplyboost/identity-service:latest
```

## Troubleshooting

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check logs
docker-compose logs postgres

# Test connection
psql -h localhost -p 5432 -U supplyboost -d identity_db
```

### Application Won't Start

1. Check Java version: `java -version` (should be 17+)
2. Check database is accessible
3. Verify Liquibase migrations ran successfully
4. Check application logs for errors

## Contributing

See [CONTRIBUTING.md](../../CONTRIBUTING.md) for guidelines.

## License

Apache 2.0 - See [LICENSE](../../LICENSE)
