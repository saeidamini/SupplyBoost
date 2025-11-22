# Technical Architecture: SupplyBoost

**Version:** 1.0
**Last Updated:** 2025-11-09
**Status:** Design Phase
**Reviewers:** Engineering Team

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [System Context](#2-system-context)
3. [Microservices Design](#3-microservices-design)
4. [Data Architecture](#4-data-architecture)
5. [Integration Patterns](#5-integration-patterns)
6. [Security Architecture](#6-security-architecture)
7. [Infrastructure & Deployment](#7-infrastructure--deployment)
8. [Observability](#8-observability)
9. [Scalability & Performance](#9-scalability--performance)
10. [Disaster Recovery](#10-disaster-recovery)

---

## 1. Architecture Overview

### 1.1 Architecture Style

**Microservices Architecture** with event-driven patterns for cross-service communication.

**Key Characteristics:**
- **Domain-Driven Design (DDD):** Services bounded by business domains
- **Event-Driven:** Asynchronous communication via Kafka event streams
- **API Gateway Pattern:** Single entry point for external clients
- **CQRS:** Command-Query Responsibility Segregation where appropriate
- **Saga Pattern:** Distributed transaction management

### 1.2 Architecture Principles

1. **Single Responsibility:** Each service owns one business capability
2. **Autonomy:** Services are independently deployable and scalable
3. **Decentralization:** No shared databases across services (bounded contexts)
4. **Resilience:** Design for failure with circuit breakers and retries
5. **Observability:** Comprehensive logging, metrics, and tracing
6. **API-First:** OpenAPI specifications before implementation
7. **Security-First:** Authentication and authorization at every layer

### 1.3 High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         External Layer                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐         ┌──────────────┐      ┌────────────┐ │
│  │   Web UI    │         │  Mobile Web  │      │   Admin    │ │
│  │  (Vue.js)   │         │   (Future)   │      │   Portal   │ │
│  └──────┬──────┘         └──────┬───────┘      └─────┬──────┘ │
│         │                       │                     │        │
│         └───────────────────────┼─────────────────────┘        │
│                                 │                              │
└─────────────────────────────────┼──────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                          API Gateway                            │
│              (Spring Cloud Gateway / Kong)                      │
│   • Routing  • Authentication  • Rate Limiting  • CORS          │
└─────────────────────────────────────────────────────────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
          ▼                       ▼                       ▼
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  Identity & IAM  │   │  Product Catalog │   │  Shopping Cart   │
│   Microservice   │   │   Microservice   │   │   Microservice   │
│                  │   │                  │   │                  │
│  • Auth/AuthZ    │   │  • Products      │   │  • Cart Mgmt     │
│  • User Profile  │   │  • Categories    │   │  • Pricing       │
│  • JWT Tokens    │   │  • Search (ES)   │   │  • Validation    │
└────────┬─────────┘   └────────┬─────────┘   └────────┬─────────┘
         │                      │                       │
         │             ┌────────┴────────┐             │
         │             ▼                 ▼             │
         │   ┌──────────────────┐   ┌──────────────────┐   │
         │   │  Order Management│   │   Inventory Svc  │   │
         │   │   Microservice   │   │                  │   │
         │   │                  │   │  • Stock Levels  │   │
         │   │  • Order CRUD    │   │  • Reservations  │   │
         │   │  • Order Status  │   │  • Alerts        │   │
         │   │  • Order Saga    │   │                  │   │
         │   └────────┬─────────┘   └────────┬─────────┘   │
         │            │                      │             │
         └────────────┼──────────────────────┼─────────────┘
                      │                      │
                      ▼                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Apache Kafka Event Streams                    │
│                                                                 │
│  Topics:                                                        │
│   • orders.created        • inventory.reserved                 │
│   • orders.confirmed      • inventory.released                 │
│   • payments.processed    • shipments.dispatched               │
│   • notifications.email   • accounting.invoices                │
└─────────────────────────────────────────────────────────────────┘
                      │                      │
          ┌───────────┼──────────────────────┼─────────────┐
          ▼           ▼                      ▼             ▼
┌─────────────┐  ┌──────────┐  ┌──────────────┐  ┌──────────────┐
│  Payment    │  │Notification│ │  Shipping    │  │  Accounting  │
│  Service    │  │  Service   │ │  Service     │  │  Service     │
│             │  │            │ │              │  │              │
│ • Stripe    │  │ • Email    │ │ • Tracking   │  │ • Invoices   │
│ • Refunds   │  │ • SMS      │ │ • Carriers   │  │ • Revenue    │
└─────────────┘  └────────────┘ └──────────────┘  └──────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      Data & Search Layer                        │
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │PostgreSQL│  │PostgreSQL│  │PostgreSQL│  │Elastic-  │       │
│  │(Orders)  │  │(Identity)│  │(Inventory)│ │search    │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   Observability & Operations                    │
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │Prometheus│  │ Grafana  │  │  Jaeger  │  │   ELK    │       │
│  │(Metrics) │  │(Dashbrd) │  │(Tracing) │  │(Logging) │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. System Context

### 2.1 External Actors

| Actor | Description | Interaction Method |
|-------|-------------|-------------------|
| **End Customer** | Retail customer purchasing products | Web browser (Vue.js SPA) |
| **Admin User** | System administrator managing catalog, orders | Admin portal (Vue.js) |
| **Payment Gateway** | Stripe API for payment processing | REST API (outbound) |
| **Email Provider** | SendGrid/AWS SES for transactional emails | SMTP/REST API (outbound) |
| **SMS Gateway** | Twilio for SMS notifications (optional) | REST API (outbound) |
| **Identity Provider** | Keycloak for OAuth2/OIDC | OIDC protocol |

### 2.2 System Boundaries

**In Scope:**
- All microservices within the SupplyBoost domain
- API Gateway and routing logic
- Internal event bus (Kafka)
- Application databases and search indices

**Out of Scope:**
- External payment processing (Stripe handles this)
- Physical warehouse management systems
- Logistics carrier systems (simulated)
- External CRM or marketing tools

---

## 3. Microservices Design

### 3.1 Service Inventory

#### **3.1.1 Identity & Access Management Service**

**Bounded Context:** User authentication, authorization, profile management

**Responsibilities:**
- User registration and email verification
- Authentication (delegate to Keycloak)
- User profile management (name, address, preferences)
- Role and permission management
- Session management (JWT issuance)

**Technology Stack:**
- Spring Boot 3.x with Spring Security
- Keycloak integration (OAuth2 Resource Server)
- PostgreSQL for user profiles
- Redis for session cache (optional)

**API Endpoints:**
```
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/logout
GET    /api/v1/users/me
PUT    /api/v1/users/me
POST   /api/v1/users/me/password
```

**Events Published:**
- `user.registered`
- `user.profile.updated`
- `user.deleted`

**Database Schema:**
```sql
users (
  id UUID PRIMARY KEY,
  keycloak_id VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  phone VARCHAR(20),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

addresses (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  type VARCHAR(20), -- shipping, billing
  street_address TEXT,
  city VARCHAR(100),
  state VARCHAR(100),
  postal_code VARCHAR(20),
  country VARCHAR(2),
  is_default BOOLEAN
)
```

---

#### **3.1.2 Product Catalog Service**

**Bounded Context:** Product information, categories, search

**Responsibilities:**
- Product CRUD operations (admin only)
- Category management
- Product search with full-text and faceting
- Product availability queries
- Product image management

**Technology Stack:**
- Spring Boot 3.x with Spring Data JPA
- PostgreSQL for product master data
- Elasticsearch for search indexing
- MinIO/S3 for image storage

**API Endpoints:**
```
GET    /api/v1/products              # List with pagination
GET    /api/v1/products/:id          # Product details
GET    /api/v1/products/search       # Full-text search
GET    /api/v1/categories            # Category tree
POST   /api/v1/products              # Admin: Create product
PUT    /api/v1/products/:id          # Admin: Update product
DELETE /api/v1/products/:id          # Admin: Delete product
```

**Events Published:**
- `product.created`
- `product.updated`
- `product.deleted`
- `product.price.changed`

**Database Schema:**
```sql
products (
  id UUID PRIMARY KEY,
  sku VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  category_id UUID REFERENCES categories(id),
  brand VARCHAR(100),
  image_urls TEXT[],
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

categories (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  parent_id UUID REFERENCES categories(id),
  path VARCHAR(500), -- Materialized path for tree queries
  level INTEGER
)
```

---

#### **3.1.3 Shopping Cart Service**

**Bounded Context:** Cart management, price calculation

**Responsibilities:**
- Add/remove/update cart items
- Calculate cart totals (subtotal, tax, shipping)
- Validate inventory availability
- Persist cart for authenticated users
- Apply promotional codes (future)

**Technology Stack:**
- Spring Boot 3.x with Spring Data Redis
- Redis for cart storage (TTL: 30 days)
- REST calls to Product Catalog for pricing
- REST calls to Inventory for availability

**API Endpoints:**
```
GET    /api/v1/cart                  # Get current user's cart
POST   /api/v1/cart/items            # Add item to cart
PUT    /api/v1/cart/items/:id        # Update quantity
DELETE /api/v1/cart/items/:id        # Remove item
DELETE /api/v1/cart                  # Clear cart
POST   /api/v1/cart/checkout         # Convert to order
```

**Data Model (Redis):**
```json
{
  "userId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "sku": "PROD-001",
      "quantity": 2,
      "priceAtAdd": 29.99
    }
  ],
  "createdAt": "2025-11-09T10:00:00Z",
  "updatedAt": "2025-11-09T10:15:00Z"
}
```

---

#### **3.1.4 Order Management Service**

**Bounded Context:** Order lifecycle, order saga orchestration

**Responsibilities:**
- Order creation from cart
- Order status management (state machine)
- Order history queries
- Order cancellation and refund coordination
- Saga orchestration for distributed transactions

**Technology Stack:**
- Spring Boot 3.x with Spring State Machine
- PostgreSQL for order persistence
- Kafka for event publishing/consuming
- Axon Framework for saga orchestration (optional)

**API Endpoints:**
```
POST   /api/v1/orders                # Create order
GET    /api/v1/orders                # List user's orders
GET    /api/v1/orders/:id            # Order details
PUT    /api/v1/orders/:id/cancel     # Cancel order
GET    /api/v1/orders/:id/status     # Order status tracking
```

**Order State Machine:**
```
PENDING → CONFIRMED → PAYMENT_PROCESSED → PREPARING → SHIPPED → DELIVERED
   ↓          ↓              ↓                ↓           ↓
CANCELLED  CANCELLED     CANCELLED        CANCELLED    FAILED
```

**Events Published:**
- `order.created`
- `order.confirmed`
- `order.cancelled`
- `order.shipped`
- `order.delivered`

**Events Consumed:**
- `inventory.reserved` (from Inventory Service)
- `payment.processed` (from Payment Service)
- `shipment.created` (from Shipping Service)

**Database Schema:**
```sql
orders (
  id UUID PRIMARY KEY,
  order_number VARCHAR(20) UNIQUE NOT NULL,
  user_id UUID NOT NULL,
  status VARCHAR(20) NOT NULL,
  subtotal DECIMAL(10,2),
  tax DECIMAL(10,2),
  shipping DECIMAL(10,2),
  total DECIMAL(10,2),
  shipping_address_id UUID,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

order_items (
  id UUID PRIMARY KEY,
  order_id UUID REFERENCES orders(id),
  product_id UUID NOT NULL,
  sku VARCHAR(50) NOT NULL,
  quantity INTEGER NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  total_price DECIMAL(10,2) NOT NULL
)

order_status_history (
  id UUID PRIMARY KEY,
  order_id UUID REFERENCES orders(id),
  from_status VARCHAR(20),
  to_status VARCHAR(20),
  reason TEXT,
  created_at TIMESTAMP
)
```

---

#### **3.1.5 Inventory Management Service**

**Bounded Context:** Stock levels, reservations, replenishment

**Responsibilities:**
- Track inventory levels per SKU
- Reserve inventory for pending orders
- Release reservations on cancellation/timeout
- Low stock alerting
- Inventory audit trail

**Technology Stack:**
- Spring Boot 3.x
- PostgreSQL with row-level locking for concurrency
- Kafka for event consumption/publishing

**API Endpoints:**
```
GET    /api/v1/inventory/:sku        # Check stock level
POST   /api/v1/inventory/reserve     # Reserve items (internal)
POST   /api/v1/inventory/release     # Release reservation (internal)
GET    /api/v1/inventory/low-stock   # Admin: Low stock report
```

**Events Published:**
- `inventory.reserved`
- `inventory.released`
- `inventory.low-stock`

**Events Consumed:**
- `order.created` → Reserve inventory
- `order.cancelled` → Release reservation
- `order.shipped` → Commit reservation

**Database Schema:**
```sql
inventory (
  id UUID PRIMARY KEY,
  sku VARCHAR(50) UNIQUE NOT NULL,
  available_quantity INTEGER NOT NULL,
  reserved_quantity INTEGER NOT NULL,
  reorder_level INTEGER DEFAULT 10,
  updated_at TIMESTAMP
)

inventory_reservations (
  id UUID PRIMARY KEY,
  sku VARCHAR(50) NOT NULL,
  order_id UUID NOT NULL,
  quantity INTEGER NOT NULL,
  status VARCHAR(20), -- PENDING, CONFIRMED, RELEASED
  expires_at TIMESTAMP,
  created_at TIMESTAMP
)

inventory_transactions (
  id UUID PRIMARY KEY,
  sku VARCHAR(50) NOT NULL,
  transaction_type VARCHAR(20), -- RESERVE, RELEASE, RESTOCK, SALE
  quantity INTEGER,
  reference_id UUID, -- order_id or shipment_id
  created_at TIMESTAMP
)
```

---

#### **3.1.6 Payment Service**

**Bounded Context:** Payment processing, refunds

**Responsibilities:**
- Integrate with Stripe payment gateway
- Process payments for orders
- Handle payment webhooks
- Process refunds for cancelled orders
- Payment audit and reconciliation

**Technology Stack:**
- Spring Boot 3.x
- Stripe Java SDK
- PostgreSQL for payment records
- Kafka for event publishing

**API Endpoints:**
```
POST   /api/v1/payments              # Create payment intent
POST   /api/v1/payments/:id/confirm  # Confirm payment
POST   /api/v1/payments/:id/refund   # Process refund
POST   /webhook/stripe               # Stripe webhook handler
```

**Events Published:**
- `payment.initiated`
- `payment.processed`
- `payment.failed`
- `payment.refunded`

**Events Consumed:**
- `order.confirmed` → Initiate payment

**Database Schema:**
```sql
payments (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  stripe_payment_intent_id VARCHAR(255),
  amount DECIMAL(10,2) NOT NULL,
  currency VARCHAR(3) DEFAULT 'USD',
  status VARCHAR(20), -- PENDING, SUCCEEDED, FAILED, REFUNDED
  failure_reason TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

---

#### **3.1.7 Shipping Service**

**Bounded Context:** Shipment creation, tracking

**Responsibilities:**
- Create shipments for confirmed orders
- Generate shipping labels (mock or API)
- Track shipment status
- Estimate delivery dates
- Integrate with carrier APIs (future)

**Technology Stack:**
- Spring Boot 3.x
- PostgreSQL for shipment records
- Kafka for event publishing

**API Endpoints:**
```
POST   /api/v1/shipments             # Create shipment (internal)
GET    /api/v1/shipments/:id         # Shipment details
GET    /api/v1/shipments/:id/track   # Tracking information
```

**Events Published:**
- `shipment.created`
- `shipment.dispatched`
- `shipment.in-transit`
- `shipment.delivered`

**Events Consumed:**
- `payment.processed` → Create shipment

---

#### **3.1.8 Notification Service**

**Bounded Context:** Multi-channel notifications

**Responsibilities:**
- Send email notifications
- Send SMS notifications (optional)
- Template-based rendering
- Notification preferences management
- Delivery tracking

**Technology Stack:**
- Spring Boot 3.x
- SendGrid/AWS SES for email
- Twilio for SMS (optional)
- Thymeleaf for email templates
- Kafka for event consumption

**API Endpoints:**
```
GET    /api/v1/notifications          # User's notification history
PUT    /api/v1/notifications/preferences
```

**Events Consumed:**
- `user.registered` → Welcome email
- `order.confirmed` → Order confirmation email
- `shipment.dispatched` → Shipment notification
- `order.delivered` → Delivery confirmation

---

#### **3.1.9 Accounting Service**

**Bounded Context:** Financial records, invoicing

**Responsibilities:**
- Generate invoices for orders
- Record revenue recognition
- Track refunds and credits
- Financial reporting
- Tax calculation (basic)

**Technology Stack:**
- Spring Boot 3.x
- PostgreSQL for financial records
- PDF generation (iText/Apache PDFBox)
- Kafka for event consumption

**API Endpoints:**
```
GET    /api/v1/invoices/:orderId     # Get invoice
GET    /api/v1/reports/revenue       # Admin: Revenue report
```

**Events Published:**
- `invoice.generated`
- `revenue.recognized`

**Events Consumed:**
- `order.confirmed` → Generate invoice
- `shipment.delivered` → Recognize revenue
- `payment.refunded` → Record credit

---

### 3.2 Service Communication Matrix

| Service | Synchronous Calls (REST) | Asynchronous Events (Kafka) |
|---------|-------------------------|----------------------------|
| **Identity** | None | Publishes: user.* |
| **Product Catalog** | None | Publishes: product.* |
| **Shopping Cart** | → Product Catalog (pricing)<br>→ Inventory (availability) | None |
| **Order Management** | → Payment (initiate)<br>→ Inventory (check) | Publishes: order.*<br>Consumes: inventory.*, payment.*, shipment.* |
| **Inventory** | None | Publishes: inventory.*<br>Consumes: order.* |
| **Payment** | → Stripe API | Publishes: payment.*<br>Consumes: order.confirmed |
| **Shipping** | None | Publishes: shipment.*<br>Consumes: payment.processed |
| **Notification** | → SendGrid API | Consumes: user.*, order.*, shipment.* |
| **Accounting** | None | Publishes: invoice.*<br>Consumes: order.*, payment.*, shipment.* |

---

## 4. Data Architecture

### 4.1 Database Strategy

**Database-per-Service Pattern:**
- Each microservice owns its database
- No direct database access across services
- Data consistency via events (eventual consistency)

**Technology Choice:**
- **PostgreSQL:** Relational data (Orders, Inventory, Accounting)
- **Redis:** Session cache, shopping carts (ephemeral data)
- **Elasticsearch:** Product search, full-text queries

### 4.2 Data Consistency Patterns

#### **Strong Consistency:**
- Within a single service's database (ACID transactions)
- Example: Order and OrderItems are transactionally consistent

#### **Eventual Consistency:**
- Across services via event streaming
- Example: Inventory reservation → Payment processing → Shipment creation

#### **Saga Pattern for Distributed Transactions:**

**Order Creation Saga (Orchestration-based):**
```
1. Order Service: Create order (PENDING)
2. Inventory Service: Reserve inventory
   ✓ Success → Continue
   ✗ Failure → Mark order CANCELLED

3. Payment Service: Process payment
   ✓ Success → Continue
   ✗ Failure → Compensate: Release inventory → Mark order CANCELLED

4. Order Service: Update order (CONFIRMED)
5. Shipping Service: Create shipment
6. Order Service: Update order (SHIPPED)
```

### 4.3 Data Synchronization

**Product Data → Elasticsearch:**
- CDC (Change Data Capture) via Debezium from PostgreSQL
- Real-time indexing on product changes
- Fallback: Scheduled batch sync (nightly)

**Cart Data → Order Data:**
- Snapshot cart items when order is created
- Cart and order are independent post-creation

---

## 5. Integration Patterns

### 5.1 API Gateway

**Technology:** Spring Cloud Gateway or Kong

**Responsibilities:**
- Request routing to appropriate microservice
- Authentication (JWT validation)
- Rate limiting (100 req/min per IP)
- CORS handling
- Request/response transformation
- Circuit breaking for downstream services

**Routing Rules:**
```
/api/v1/auth/**       → Identity Service
/api/v1/users/**      → Identity Service
/api/v1/products/**   → Product Catalog Service
/api/v1/cart/**       → Shopping Cart Service
/api/v1/orders/**     → Order Management Service
/api/v1/payments/**   → Payment Service
/api/v1/shipments/**  → Shipping Service
```

### 5.2 Event Streaming (Kafka)

**Topic Naming Convention:**
`{domain}.{entity}.{event-type}`

**Example Topics:**
- `orders.order.created`
- `inventory.stock.reserved`
- `payments.payment.processed`
- `notifications.email.sent`

**Event Schema (CloudEvents standard):**
```json
{
  "specversion": "1.0",
  "type": "com.supplyboost.orders.order.created",
  "source": "/order-service",
  "id": "uuid-1234",
  "time": "2025-11-09T12:00:00Z",
  "datacontenttype": "application/json",
  "data": {
    "orderId": "uuid",
    "userId": "uuid",
    "total": 99.99,
    "items": [...]
  }
}
```

**Message Guarantees:**
- **At-least-once delivery** (consumers must be idempotent)
- Event deduplication via unique event IDs
- Retention: 7 days (configurable)

### 5.3 Service Discovery

**Technology:** Kubernetes DNS or Spring Cloud Netflix Eureka

**Local Development:** Docker Compose service names
**Production:** Kubernetes service discovery

### 5.4 Circuit Breaker Pattern

**Technology:** Resilience4j

**Configuration Example:**
```yaml
resilience4j.circuitbreaker:
  instances:
    inventoryService:
      failureRateThreshold: 50
      waitDurationInOpenState: 60000
      slidingWindowSize: 10
      minimumNumberOfCalls: 5
```

**Fallback Strategies:**
- Return cached data (if available)
- Return default/empty response with degraded flag
- Fail fast with user-friendly error message

---

## 6. Security Architecture

### 6.1 Authentication & Authorization

**Identity Provider:** Keycloak (OAuth2/OIDC)

**Flow:**
```
1. User → Frontend: Login credentials
2. Frontend → Keycloak: OAuth2 authorization code flow
3. Keycloak → Frontend: Access Token (JWT) + Refresh Token
4. Frontend → API Gateway: Request + Bearer Token
5. API Gateway: Validates JWT signature and expiration
6. API Gateway → Microservice: Forwards request with user context
```

**JWT Claims:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "roles": ["CUSTOMER"],
  "exp": 1699545600,
  "iat": 1699542000,
  "iss": "https://keycloak.supplyboost.com"
}
```

**Authorization:**
- **Role-Based Access Control (RBAC)**
- Roles: `CUSTOMER`, `ADMIN`, `WAREHOUSE_STAFF`
- Method-level security with `@PreAuthorize` annotations

### 6.2 Network Security

**TLS/HTTPS:**
- All external communications use TLS 1.3
- Certificate management via Let's Encrypt (production)

**Internal Communication:**
- Service-to-service within Kubernetes cluster: Plain HTTP (behind firewall)
- Optional: Istio service mesh for mTLS

### 6.3 Data Security

**Encryption at Rest:**
- Database encryption (PostgreSQL pgcrypto)
- Sensitive fields encrypted: payment info, PII

**Encryption in Transit:**
- TLS for all external APIs
- Kafka: SASL_SSL for production

**Secret Management:**
- Kubernetes Secrets for sensitive config
- External: HashiCorp Vault (future consideration)

### 6.4 API Security

**Input Validation:**
- Bean Validation (JSR-303) on all request DTOs
- SQL injection prevention via parameterized queries (JPA)
- XSS prevention via output encoding

**Rate Limiting:**
- API Gateway: 100 requests/minute per IP
- Per-endpoint limits for sensitive operations (e.g., login: 5/min)

**CORS Policy:**
```yaml
allowed-origins: ["https://supplyboost.com"]
allowed-methods: ["GET", "POST", "PUT", "DELETE"]
allowed-headers: ["Authorization", "Content-Type"]
allow-credentials: true
```

---

## 7. Infrastructure & Deployment

### 7.1 Local Development

**Technology:** Docker Compose

**Services:**
```yaml
services:
  postgres-identity: ...
  postgres-orders: ...
  postgres-inventory: ...
  redis: ...
  elasticsearch: ...
  kafka: ...
  zookeeper: ...
  keycloak: ...
  identity-service: ...
  product-service: ...
  order-service: ...
  # ... all microservices
  api-gateway: ...
  frontend: ...
```

**Developer Experience:**
- `docker-compose up` starts entire stack
- Live reload for code changes
- Seeded with sample data
- Accessible at `http://localhost:8080`

### 7.2 Production Deployment

**Technology:** Kubernetes (GKE, EKS, or AKS)

**Deployment Strategy:**
- **Blue-Green Deployment** for zero-downtime releases
- **Canary Releases** for gradual rollout (10% → 50% → 100%)

**Kubernetes Resources per Microservice:**
```yaml
- Deployment (with rolling update strategy)
- Service (ClusterIP)
- HorizontalPodAutoscaler (HPA)
- ConfigMap (application.yaml)
- Secret (database credentials, API keys)
- Ingress (API Gateway only)
```

**Example Deployment Manifest:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    spec:
      containers:
      - name: order-service
        image: supplyboost/order-service:1.0.0
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
```

### 7.3 CI/CD Pipeline

**Technology:** GitHub Actions or GitLab CI

**Pipeline Stages:**
```
1. Code Checkout
2. Compile & Build (Maven)
3. Unit Tests (JUnit)
4. Static Code Analysis (SonarQube)
5. Build Docker Image
6. Integration Tests (Testcontainers)
7. Security Scan (Trivy)
8. Push to Registry (Docker Hub / ECR)
9. Deploy to Staging (Kubernetes)
10. Smoke Tests
11. Deploy to Production (manual approval)
```

**Build Time Target:** < 10 minutes

### 7.4 Environment Strategy

| Environment | Purpose | Infrastructure | Data |
|-------------|---------|----------------|------|
| **Local** | Developer laptops | Docker Compose | Mock/sample data |
| **CI** | Automated testing | Ephemeral containers | Test fixtures |
| **Staging** | Pre-production validation | Kubernetes (1 replica) | Anonymized production data |
| **Production** | Live system | Kubernetes (3+ replicas) | Real customer data |

---

## 8. Observability

### 8.1 Logging

**Technology:** ELK Stack (Elasticsearch, Logstash, Kibana) or EFK (Fluentd)

**Log Format:** Structured JSON
```json
{
  "timestamp": "2025-11-09T12:00:00Z",
  "level": "INFO",
  "service": "order-service",
  "traceId": "abc123",
  "spanId": "def456",
  "userId": "user-uuid",
  "message": "Order created successfully",
  "orderId": "order-uuid",
  "environment": "production"
}
```

**Log Levels:**
- **ERROR:** System errors, exceptions
- **WARN:** Deprecated API usage, performance degradation
- **INFO:** Business events (order created, payment processed)
- **DEBUG:** Development/troubleshooting (disabled in production)

**Retention:** 30 days

### 8.2 Metrics

**Technology:** Prometheus + Grafana

**Key Metrics (RED Method):**
- **Rate:** Requests per second per endpoint
- **Errors:** Error rate (5xx responses)
- **Duration:** Response time (p50, p95, p99)

**Additional Metrics:**
- JVM metrics (heap, GC, threads)
- Database connection pool stats
- Kafka consumer lag
- Business metrics (orders/hour, revenue/day)

**Example Dashboard:**
- Service health overview
- API response times by endpoint
- Error rates by service
- Order funnel conversion rates

### 8.3 Distributed Tracing

**Technology:** Jaeger or Zipkin

**Integration:** Spring Cloud Sleuth

**Trace Propagation:**
- HTTP headers: `X-B3-TraceId`, `X-B3-SpanId`
- Kafka message headers: `traceparent`

**Use Cases:**
- Debugging slow requests across services
- Identifying bottlenecks in saga flows
- Understanding service dependencies

**Example Trace:**
```
Trace: order-creation-flow (2.3s total)
├─ API Gateway (50ms)
├─ Order Service: Create order (200ms)
├─ Inventory Service: Reserve stock (150ms)
│  └─ PostgreSQL query (120ms)
├─ Payment Service: Process payment (1.5s)
│  └─ Stripe API call (1.4s)
└─ Notification Service: Send email (400ms)
   └─ SendGrid API call (380ms)
```

### 8.4 Alerting

**Technology:** Prometheus Alertmanager

**Alert Rules:**
```yaml
- alert: HighErrorRate
  expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
  for: 5m
  annotations:
    summary: "High error rate detected"

- alert: ServiceDown
  expr: up{job="order-service"} == 0
  for: 2m
  annotations:
    summary: "Order service is down"

- alert: DatabaseConnectionPoolExhausted
  expr: hikaricp_connections_active / hikaricp_connections_max > 0.9
  for: 5m
```

**Notification Channels:**
- Slack (development)
- PagerDuty (production critical alerts)
- Email (low-priority alerts)

---

## 9. Scalability & Performance

### 9.1 Horizontal Scaling

**Stateless Services:**
- All microservices are stateless
- Horizontal Pod Autoscaler (HPA) based on CPU/memory
- Scale from 2 to 10 replicas based on load

**Stateful Services:**
- PostgreSQL: Read replicas for query distribution
- Kafka: Partitioning for parallel consumption
- Redis: Cluster mode for cart data

### 9.2 Caching Strategy

**Layers:**
```
Browser → CDN → API Gateway → Redis Cache → Microservice → Database
```

**Cache Candidates:**
- Product catalog (TTL: 1 hour)
- Category tree (TTL: 24 hours)
- User sessions (TTL: session lifetime)
- Shopping carts (TTL: 30 days)

**Cache Invalidation:**
- Event-driven: `product.updated` → invalidate product cache
- Time-based: TTL expiration

### 9.3 Database Optimization

**Indexing Strategy:**
```sql
-- Orders: Index on user_id for "my orders" queries
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- Orders: Index on created_at for date range queries
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- Products: Full-text search index
CREATE INDEX idx_products_search ON products USING gin(to_tsvector('english', name || ' ' || description));
```

**Connection Pooling:**
- HikariCP with max pool size: 20 connections per instance
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes

### 9.4 Async Processing

**Use Cases:**
- Email sending (fire-and-forget)
- Report generation
- Data export
- Image processing for product photos

**Technology:**
- Spring `@Async` for simple cases
- Kafka for durable async processing
- Scheduled jobs for batch operations

---

## 10. Disaster Recovery

### 10.1 Backup Strategy

**Databases:**
- Automated daily backups (PostgreSQL pg_dump)
- Retention: 30 days
- Tested restore procedure (monthly drill)

**Kafka:**
- Event replay capability (7-day retention)
- Consumer offset management

### 10.2 Failure Scenarios

| Scenario | Impact | Recovery Strategy |
|----------|--------|------------------|
| **Single service failure** | Degraded functionality | Auto-restart (Kubernetes), circuit breaker prevents cascade |
| **Database failure** | Service unavailable | Failover to standby replica (manual or auto) |
| **Kafka broker failure** | Message delay | Kafka cluster rebalancing (automatic) |
| **Payment gateway outage** | No new orders | Queue payment requests, process when restored |
| **Complete region failure** | Full outage | Multi-region deployment (future), restore from backup |

### 10.3 Data Recovery

**Saga Compensation:**
- All saga steps have compensating transactions
- Example: Payment failed → Release inventory reservation

**Event Sourcing:**
- Rebuild state from event log if needed
- Kafka topic replay for order state reconstruction

---

## Appendix: Technology Decisions

| Component | Technology | Rationale |
|-----------|-----------|-----------|
| **Backend Framework** | Spring Boot | Industry standard, rich ecosystem, excellent Spring Cloud support |
| **API Gateway** | Spring Cloud Gateway | Native Spring integration, reactive, filters |
| **Message Broker** | Apache Kafka | Event streaming, durability, scalability, industry standard |
| **Database** | PostgreSQL | ACID compliance, JSON support, mature, open-source |
| **Cache** | Redis | Fast, versatile (cache + session store + cart storage) |
| **Search** | Elasticsearch | Full-text search, faceting, analytics |
| **Identity** | Keycloak | Open-source, OAuth2/OIDC, battle-tested |
| **Monitoring** | Prometheus + Grafana | Cloud-native standard, rich ecosystem |
| **Tracing** | Jaeger | CNCF project, OpenTelemetry compatible |
| **Logging** | ELK Stack | Powerful search, visualization, scalable |
| **Container** | Docker | Industry standard |
| **Orchestration** | Kubernetes | De facto standard, portable, rich ecosystem |
| **CI/CD** | GitHub Actions | Integrated with repo, free for open-source |

---

## Document Maintenance

This document should be reviewed and updated:
- After major architecture decisions
- Before each release milestone
- When technology stack changes
- Quarterly architecture review

**Next Review Date:** 2025-12-09

