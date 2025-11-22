# ADR-001: Adopt Microservices Architecture

**Status:** Accepted
**Date:** 2025-11-09
**Deciders:** Engineering Team
**Technical Story:** Initial architecture design

## Context

SupplyBoost aims to be an educational platform demonstrating modern software architecture patterns for supply chain management. The system needs to handle multiple business domains (identity, catalog, orders, inventory, payments, shipping, notifications, accounting) with distinct concerns.

We need an architecture that:
- Demonstrates real-world enterprise patterns
- Allows independent scaling of components
- Enables learning about distributed systems
- Reflects industry best practices
- Handles domain complexity appropriately

The two primary architectural styles considered are:
1. **Monolithic Architecture:** All functionality in a single deployable unit
2. **Microservices Architecture:** Decomposed into independently deployable services

## Decision

We will adopt a **microservices architecture** where the system is decomposed into independent services, each responsible for a specific business capability.

**Key principles:**
- Each microservice owns a bounded context aligned with Domain-Driven Design (DDD)
- Services communicate via REST APIs (synchronous) and Kafka events (asynchronous)
- Each service has its own database (database-per-service pattern)
- Services are independently deployable and scalable
- API Gateway provides single entry point for external clients

**Microservices to be implemented:**
1. Identity & Access Management
2. Product Catalog
3. Shopping Cart
4. Order Management
5. Inventory Management
6. Payment Processing
7. Shipping
8. Notification
9. Accounting

## Consequences

### Positive

- **Educational Value:** Demonstrates distributed systems concepts (CAP theorem, eventual consistency, saga pattern)
- **Independent Scaling:** Can scale inventory service independently of payment service based on load
- **Technology Diversity:** Can use different tech stacks where appropriate (though we'll standardize on Java/Spring Boot)
- **Team Autonomy:** In a team environment, different teams could own different services
- **Fault Isolation:** Failure in notification service doesn't bring down order creation
- **Deployment Flexibility:** Can deploy only changed services, reducing deployment risk
- **Clear Boundaries:** Forces clear thinking about domain boundaries and service contracts

### Negative

- **Increased Complexity:** Distributed systems are inherently more complex than monoliths
  - Network latency and failures
  - Distributed transactions (saga pattern required)
  - Eventual consistency challenges
  - Debugging across multiple services
- **Operational Overhead:** More services to deploy, monitor, and maintain
- **Data Consistency:** No ACID transactions across services, must implement saga pattern
- **Development Environment:** Requires Docker Compose to run all services locally
- **Testing Complexity:** Integration testing requires multiple services running
- **Learning Curve:** Steeper for developers unfamiliar with distributed systems

### Neutral

- **Infrastructure Requirements:** Requires container orchestration (Kubernetes) for production
- **Tooling Needs:** Requires centralized logging, tracing, and monitoring from day one
- **API Contract Management:** Need to carefully manage service-to-service contracts

## Alternatives Considered

### Alternative 1: Modular Monolith

**Description:** Single deployable application with well-defined internal module boundaries. Modules communicate via in-process calls but maintain clear boundaries.

**Pros:**
- Simpler deployment (single artifact)
- ACID transactions across modules
- Easier debugging and testing
- Lower infrastructure costs
- No network latency between modules

**Cons:**
- Cannot scale modules independently
- Single point of failure (whole app goes down)
- Less educational value (doesn't teach distributed systems)
- Technology stack locked for all modules
- Eventual migration to microservices more difficult

**Reason for rejection:** While a modular monolith is often the right choice for many projects, this project's primary goal is **educational**. The learning value of implementing microservices, dealing with distributed systems challenges, and understanding modern cloud-native architectures outweighs the simplicity benefits of a monolith.

### Alternative 2: Serverless Functions (FaaS)

**Description:** Implement each capability as serverless functions (AWS Lambda, Azure Functions) rather than persistent microservices.

**Pros:**
- Auto-scaling built-in
- Pay-per-use pricing model
- No infrastructure management
- Perfect for event-driven workloads

**Cons:**
- Vendor lock-in (difficult to run locally)
- Cold start latency issues
- Limited execution time (timeout constraints)
- Stateless by nature (requires external state management)
- Cost unpredictability for learning project
- Less educational for traditional enterprise environments

**Reason for rejection:** Serverless is excellent for specific use cases, but the majority of enterprise applications still run on container-based microservices (Kubernetes). This project aims to teach broadly applicable patterns.

### Alternative 3: Service-Oriented Architecture (SOA)

**Description:** Traditional SOA with Enterprise Service Bus (ESB) for integration.

**Pros:**
- Well-established pattern with mature tooling
- Centralized governance and routing (ESB)
- Supports legacy integration

**Cons:**
- ESB becomes a single point of failure
- Heavy tooling and complexity (WS-*, SOAP)
- Less aligned with modern cloud-native practices
- ESB can become a bottleneck
- Outdated approach (industry moving to microservices)

**Reason for rejection:** SOA is largely superseded by microservices. Modern organizations use lightweight protocols (REST, gRPC) and decentralized messaging (Kafka) rather than heavy ESBs.

## Implementation Notes

### Service Size Guidelines

Services should be:
- **Small enough** to be understood by one developer
- **Large enough** to provide business value independently
- **Focused** on a single business capability

### Communication Patterns

- **Synchronous (REST):** Use for real-time queries where immediate response needed (e.g., cart checking inventory availability)
- **Asynchronous (Kafka):** Use for event notification where eventual consistency acceptable (e.g., order created → send email)

### Deployment Strategy

- **Local Development:** Docker Compose (all services on one machine)
- **Production:** Kubernetes with Helm charts

## References

- [Microservices by Martin Fowler](https://martinfowler.com/articles/microservices.html)
- [Building Microservices by Sam Newman](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
- [Spring Microservices in Action](https://www.manning.com/books/spring-microservices-in-action-second-edition)
- Related: ADR-002 (Event-Driven Communication), ADR-003 (Database-per-Service)

