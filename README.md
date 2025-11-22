# SupplyBoost

![Banner](docs/assets/banner.jpg)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

**SupplyBoost** is an educational platform demonstrating enterprise-grade supply chain management through a distributed microservices architecture. This project serves as a comprehensive learning resource for modern software engineering practices, cloud-native patterns, and real-world system design challenges.

> **Project Status:** 🚧 **Planning Phase** - Repository restructuring and architecture design in progress.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Development Roadmap](#development-roadmap)
- [Contributing](#contributing)
- [Documentation](#documentation)
- [License](#license)

---

## Overview

### Purpose

SupplyBoost bridges the gap between theoretical knowledge and practical implementation of distributed systems. While countless resources teach microservices, event-driven architecture, and cloud technologies in isolation, few demonstrate how these patterns work together in a cohesive, production-like system.

**This project aims to:**
- Provide a realistic, non-trivial example of microservices architecture
- Demonstrate modern software engineering practices and patterns
- Serve as a learning platform for developers transitioning to distributed systems
- Showcase the full lifecycle: design → development → deployment → operations

### Target Audience

- **Mid-level developers** seeking hands-on experience with microservices
- **DevOps engineers** exploring containerization and orchestration
- **Architecture enthusiasts** studying system design patterns
- **Students and educators** looking for real-world examples beyond "todo apps"

### What Makes This Different?

Unlike toy applications, SupplyBoost tackles real complexity:
- **Multiple business domains:** Identity, catalog, orders, inventory, payments, shipping, accounting
- **Distributed transactions:** Saga pattern for cross-service workflows
- **Event-driven architecture:** Asynchronous communication via Kafka
- **Production-ready practices:** Monitoring, logging, tracing, security, testing

---

## Architecture

SupplyBoost follows a **microservices architecture** with event-driven patterns.

```
┌─────────────┐         ┌──────────────┐         ┌────────────┐
│   Web UI    │────────▶│ API Gateway  │────────▶│ Services   │
│  (Vue.js)   │         │   (Spring)   │         │ (9 µsvcs)  │
└─────────────┘         └──────────────┘         └────────────┘
                                │                       │
                                │                       ▼
                                │              ┌─────────────────┐
                                │              │  Apache Kafka   │
                                │              │ (Event Streams) │
                                │              └─────────────────┘
                                │                       │
                                ▼                       ▼
                        ┌──────────────┐       ┌───────────────┐
                        │  Keycloak    │       │  PostgreSQL   │
                        │   (Auth)     │       │  Elasticsearch│
                        └──────────────┘       └───────────────┘
```

### Microservices

| Service | Responsibility | Technology |
|---------|---------------|------------|
| **Identity Service** | User authentication, authorization, profiles | Spring Boot + Keycloak |
| **Product Catalog** | Product management, categories, search | Spring Boot + Elasticsearch |
| **Shopping Cart** | Cart management, pricing calculations | Spring Boot + Redis |
| **Order Management** | Order lifecycle, saga orchestration | Spring Boot + PostgreSQL |
| **Inventory** | Stock tracking, reservations, alerts | Spring Boot + PostgreSQL |
| **Payment** | Payment processing, Stripe integration | Spring Boot + PostgreSQL |
| **Shipping** | Shipment creation, tracking | Spring Boot + PostgreSQL |
| **Notification** | Email/SMS notifications | Spring Boot + SendGrid |
| **Accounting** | Invoicing, revenue recognition | Spring Boot + PostgreSQL |

**For detailed architecture, see:** [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

---

## Features

### Current Phase: MVP (Minimum Viable Product)

**User Journey:**
1. ✅ User registration and authentication
2. ✅ Browse product catalog with search and filtering
3. ✅ Add products to shopping cart
4. ✅ Checkout and payment processing
5. ✅ Order tracking and status updates
6. ✅ Email notifications at key events
7. ✅ Invoice generation and delivery

### Planned Features

- Advanced inventory management with low-stock alerts
- Order modification and cancellation workflows
- Customer reviews and ratings
- Promotional codes and discounts
- Admin portal for catalog and order management
- Analytics dashboards

**See:** [docs/ROADMAP.md](docs/ROADMAP.md) for complete feature timeline

---

## Technology Stack

### Backend

- **Framework:** Spring Boot 3.2, Spring Cloud
- **Language:** Java 17+
- **Build Tool:** Maven 3.8+
- **API Gateway:** Spring Cloud Gateway
- **Service Discovery:** Kubernetes DNS / Eureka

### Data Layer

- **Database:** PostgreSQL (per-service databases)
- **Cache:** Redis
- **Search:** Elasticsearch
- **Message Broker:** Apache Kafka

### Frontend

- **Framework:** Vue.js 3 + TypeScript
- **Build Tool:** Vite
- **State Management:** Pinia

### Infrastructure

- **Containerization:** Docker
- **Orchestration:** Kubernetes
- **Local Dev:** Docker Compose
- **CI/CD:** GitHub Actions

### Observability

- **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana)
- **Metrics:** Prometheus + Grafana
- **Tracing:** Jaeger
- **APM:** Spring Boot Actuator + Micrometer

### Security

- **Identity Provider:** Keycloak (OAuth2/OIDC)
- **Authentication:** JWT tokens
- **Authorization:** Role-based access control (RBAC)

---

## Getting Started

### Prerequisites

Ensure you have the following installed:

- **Java 17+** ([OpenJDK](https://openjdk.org/) or [Azul Zulu](https://www.azul.com/downloads/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Docker 20+** and **Docker Compose** ([Get Docker](https://docs.docker.com/get-docker/))
- **Node.js 18+** (for frontend) ([Download](https://nodejs.org/))
- **Git** ([Download](https://git-scm.com/))

### Quick Start

**Note:** The project is currently in the planning/restructuring phase. The following instructions will be functional after Week 1 of the roadmap.

```bash
# 1. Clone the repository
git clone https://github.com/deepkulk/SupplyBoost.git
cd SupplyBoost

# 2. Start infrastructure services (PostgreSQL, Kafka, Redis, etc.)
cd infrastructure/docker-compose
docker-compose up -d

# 3. Build all microservices
cd ../..
mvn clean install

# 4. Run services (once implemented)
# Individual services will have their own run instructions

# 5. Access the application
# Web UI: http://localhost:8080
# API Gateway: http://localhost:8080/api
# Grafana: http://localhost:3000
# Kibana: http://localhost:5601
```

**First-time setup:** See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed development environment setup.

---

## Project Structure

```
SupplyBoost/
├── services/                      # Microservices
│   ├── identity-service/          # User authentication & authorization
│   ├── product-catalog-service/   # Product management & search
│   ├── shopping-cart-service/     # Shopping cart management
│   ├── order-management-service/  # Order lifecycle & saga orchestration
│   ├── inventory-service/         # Stock tracking & reservations
│   ├── payment-service/           # Payment processing
│   ├── shipping-service/          # Shipment management
│   ├── notification-service/      # Email/SMS notifications
│   └── accounting-service/        # Invoicing & financial records
│
├── frontend/                      # Frontend applications
│   └── web-app/                   # Vue.js SPA
│
├── infrastructure/                # Infrastructure as Code
│   ├── docker-compose/            # Local development environment
│   └── kubernetes/                # K8s manifests (Helm charts)
│
├── docs/                          # Documentation
│   ├── PRD.md                     # Product Requirements Document
│   ├── ARCHITECTURE.md            # Technical architecture
│   ├── ROADMAP.md                 # Development roadmap
│   ├── adr/                       # Architecture Decision Records
│   ├── assets/                    # Images, diagrams
│   └── archive/                   # Historical documents
│
├── scripts/                       # Utility scripts
│   ├── setup-dev-env.sh           # Development environment setup
│   ├── run-all-tests.sh           # Run all tests
│   └── deploy.sh                  # Deployment automation
│
├── pom.xml                        # Maven parent POM
├── README.md                      # This file
├── CONTRIBUTING.md                # Contribution guidelines
└── LICENSE                        # Apache 2.0 license
```

---

## Development Roadmap

The project follows a 12-week development plan divided into three phases:

### Phase 1: Foundation (Weeks 1-4)
- ✅ Repository restructuring
- ✅ Comprehensive planning documents
- ⏳ CI/CD pipeline setup
- ⏳ Core services scaffolding (Identity, Product Catalog, Inventory)

### Phase 2: Core Features (Weeks 5-8)
- ⏳ Shopping cart and order creation
- ⏳ Payment and shipping services
- ⏳ Event-driven saga implementation
- ⏳ Frontend development (Vue.js)

### Phase 3: Production Readiness (Weeks 9-12)
- ⏳ Observability stack (logging, metrics, tracing)
- ⏳ Security hardening
- ⏳ Performance testing and optimization
- ⏳ Kubernetes deployment
- ⏳ Operational documentation

**Detailed roadmap:** [docs/ROADMAP.md](docs/ROADMAP.md)

**Progress Tracking:** See [GitHub Projects](../../projects) for current sprint status.

---

## Contributing

We welcome contributions! Whether you're fixing bugs, improving documentation, or proposing new features, your help is appreciated.

### How to Contribute

1. **Read** [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines
2. **Fork** the repository
3. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
4. **Commit** your changes with clear messages
5. **Push** to your branch (`git push origin feature/amazing-feature`)
6. **Open** a Pull Request

### Code of Conduct

This project adheres to the Contributor Covenant [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

### Development Workflow

```bash
# Run tests before committing
mvn test

# Format code (Spotless)
mvn spotless:apply

# Check code coverage
mvn verify
open target/site/jacoco/index.html
```

---

## Documentation

### Core Documents

| Document | Description |
|----------|-------------|
| [PRD.md](docs/PRD.md) | Product Requirements Document - what we're building |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | System architecture and design patterns |
| [ROADMAP.md](docs/ROADMAP.md) | 12-week development plan |
| [RESTRUCTURING_PLAN.md](docs/RESTRUCTURING_PLAN.md) | Repository cleanup strategy |

### Architecture Decision Records (ADRs)

Important architectural decisions are documented in [docs/adr/](docs/adr/):

- [ADR-001: Microservices Architecture](docs/adr/ADR-001-microservices-architecture.md)
- [ADR-002: Event-Driven Communication](docs/adr/ADR-002-event-driven-communication.md) *(planned)*
- [ADR-003: Database-per-Service](docs/adr/ADR-003-database-per-service.md) *(planned)*
- [ADR-005: Monorepo Structure](docs/adr/ADR-005-monorepo-vs-multirepo.md)

### API Documentation

Once services are implemented, API documentation will be available at:
- **Swagger UI:** `http://localhost:8080/swagger-ui`
- **OpenAPI Spec:** `http://localhost:8080/api-docs`

---

## Diagrams

### User Purchase Flow
![User Purchase Flow](docs/assets/diagrams/BuyProductFromRetail.svg)

### Supply Chain Process
![Supply Chain Process](docs/assets/diagrams/SupplyChainProcess.drawio.svg)

---

## FAQ

**Q: Is this production-ready?**
A: Not yet. This is an educational project. While we follow production best practices, it's designed for learning, not commercial deployment.

**Q: Can I use this for my startup/company?**
A: Yes, under the Apache 2.0 license. However, you'll need significant additional work for true production readiness (security audit, scaling, compliance, etc.).

**Q: Why not just use a monolith?**
A: Monoliths are often the right choice! This project explicitly chooses microservices for **educational value**. See [ADR-001](docs/adr/ADR-001-microservices-architecture.md) for the full reasoning.

**Q: How can I contribute?**
A: Check [CONTRIBUTING.md](CONTRIBUTING.md) and look for issues labeled `good first issue` or `help wanted`.

**Q: What's the deployment strategy?**
A: Local development uses Docker Compose. Production deployment targets Kubernetes (GKE/EKS/AKS). See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md#infrastructure--deployment).

---

## Community

- **Issues:** [GitHub Issues](../../issues) - Bug reports and feature requests
- **Discussions:** [GitHub Discussions](../../discussions) - Questions and ideas
- **Twitter:** [@saeid_amini](https://twitter.com/saeid_amini) - Project updates

---

## Acknowledgments

- Inspired by [eShopOnContainers](https://github.com/dotnet-architecture/eShopOnContainers) (Microsoft)
- Architecture patterns from *Building Microservices* by Sam Newman
- DDD concepts from *Domain-Driven Design* by Eric Evans

---

## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

Copyright © 2025 SupplyBoost Contributors

---

## Star History

If you find this project helpful, please consider giving it a ⭐! It helps others discover the project.

[![Star History Chart](https://api.star-history.com/svg?repos=deepkulk/SupplyBoost&type=Date)](https://star-history.com/#deepkulk/SupplyBoost&Date)

---

**Built with ❤️ for the developer community**

