# SupplyBoost Infrastructure

This directory contains the Docker Compose configuration for running all infrastructure services required for local development.

## Services

The stack includes the following services:

### Data Stores
- **PostgreSQL** (Port 5432) - Primary database for all microservices
- **Redis** (Port 6379) - Caching and session storage
- **Elasticsearch** (Port 9200) - Product search and log storage

### Message Broker
- **Kafka** (Port 9092/29092) - Event streaming
- **Zookeeper** (Port 2181) - Kafka coordination
- **Kafka UI** (Port 8090) - Kafka management interface

### Identity & Security
- **Keycloak** (Port 8180) - Identity and access management

### Observability
- **Prometheus** (Port 9090) - Metrics collection
- **Grafana** (Port 3000) - Metrics visualization
  - Username: `admin`
  - Password: `admin`
- **Jaeger** (Port 16686) - Distributed tracing
- **Kibana** (Port 5601) - Log visualization
- **Logstash** (Port 5000) - Log processing

### Development Tools
- **MailDev** (Port 1080/1025) - Email testing

## Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### Start All Services

```bash
cd infrastructure/docker-compose
docker-compose up -d
```

### Check Service Status

```bash
docker-compose ps
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
```

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Volumes

```bash
docker-compose down -v
```

## Service Endpoints

| Service | URL | Credentials |
|---------|-----|-------------|
| Keycloak Admin | http://localhost:8180 | admin / admin |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | - |
| Jaeger UI | http://localhost:16686 | - |
| Kibana | http://localhost:5601 | - |
| Kafka UI | http://localhost:8090 | - |
| Elasticsearch | http://localhost:9200 | - |
| MailDev | http://localhost:1080 | - |
| PostgreSQL | localhost:5432 | supplyboost / supplyboost_dev_password |
| Redis | localhost:6379 | - |

## Database Setup

The PostgreSQL container automatically creates the following databases on first startup:

- `identity_db` - Identity service
- `product_catalog_db` - Product catalog service
- `inventory_db` - Inventory service
- `order_management_db` - Order management service
- `payment_db` - Payment service
- `shipping_db` - Shipping service
- `accounting_db` - Accounting service
- `keycloak` - Keycloak identity provider

## Keycloak Configuration

After starting Keycloak for the first time:

1. Access http://localhost:8180
2. Login with `admin` / `admin`
3. Create a new realm called `supplyboost`
4. Create a client for each microservice
5. Configure OAuth2/OIDC settings

## Kafka Topics

Kafka is configured to auto-create topics. The following topics are used:

- `order.created`
- `order.updated`
- `payment.processed`
- `payment.failed`
- `inventory.reserved`
- `inventory.released`
- `shipment.created`
- `notification.email`

## Troubleshooting

### Services Won't Start

```bash
# Check for port conflicts
netstat -tuln | grep LISTEN

# Clean up and restart
docker-compose down -v
docker-compose up -d
```

### PostgreSQL Connection Issues

```bash
# Check if PostgreSQL is ready
docker-compose exec postgres pg_isready -U supplyboost

# Connect to PostgreSQL
docker-compose exec postgres psql -U supplyboost -d supplyboost
```

### Elasticsearch Memory Issues

If Elasticsearch fails to start with memory errors:

```bash
# Increase Docker memory limit to at least 4GB
# Or reduce Elasticsearch memory in docker-compose.yml
```

### Kafka Connection Issues

```bash
# Check Kafka logs
docker-compose logs kafka

# Test Kafka connection
docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
```

## Development Tips

### Accessing Services from Host

All services are accessible from your host machine using `localhost` and the exposed ports.

### Accessing Services from Containers

When microservices running in Docker need to connect:
- Use service name as hostname (e.g., `postgres`, `kafka`, `redis`)
- Use internal ports (e.g., `postgres:5432`, `kafka:9092`)

### Accessing Host from Containers

Use `host.docker.internal` to access services running on your host machine from within containers.

### Data Persistence

All data is persisted in Docker volumes. To reset:

```bash
docker-compose down -v
```

## Resource Requirements

Minimum system requirements:
- CPU: 4 cores
- RAM: 8 GB
- Disk: 20 GB free space

Recommended:
- CPU: 8+ cores
- RAM: 16 GB
- Disk: 50 GB free space

## Production Deployment

⚠️ **Warning:** This configuration is for local development only. Do NOT use in production without:

- Proper security hardening
- SSL/TLS encryption
- Strong passwords and secrets management
- Network segmentation
- Resource limits and monitoring
- Backup and disaster recovery
