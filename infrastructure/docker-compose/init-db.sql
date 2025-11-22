-- Create databases for each microservice
CREATE DATABASE identity_db;
CREATE DATABASE product_catalog_db;
CREATE DATABASE inventory_db;
CREATE DATABASE order_management_db;
CREATE DATABASE payment_db;
CREATE DATABASE shipping_db;
CREATE DATABASE accounting_db;
CREATE DATABASE keycloak;

-- Create users for each service (optional, for better security)
-- All services use the same user for simplicity in development
-- In production, each service should have its own user

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE identity_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE product_catalog_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE order_management_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE shipping_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE accounting_db TO supplyboost;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO supplyboost;

-- Display created databases
\l
