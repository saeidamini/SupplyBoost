-- Create separate databases for each microservice
CREATE DATABASE order_management_db;
CREATE DATABASE payment_db;
CREATE DATABASE shipping_db;
CREATE DATABASE notification_db;
CREATE DATABASE accounting_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE order_management_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE shipping_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE accounting_db TO postgres;
