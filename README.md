# SupplyBoost

SupplyBoost is a supply chain management application that has one Gateway and five Microservice. 
This application uses eureka for service discovery and [OAuth 2.0](https://oauth.net/2/) for authentication.

![Supply Chain Process](resource/SupplyChainProcess.drawio.svg)

## SupplyBoost's modules/Microservice

SupplyBoost have these modules:
- **Retail**
  - In Stores, they sales Goods to Customer (use by Retailer).
- **Distribute**
  - The Goods are Delivered by the Retailer (use by Distributor).
- **Notification**
  - Send all type of notification(SMS, Email, ...) in other modules.
- **Supply**
    - They receive the requests and send them to the factories (use by Supplier).
- **Manufacture**
    - They Plan for Production (use by Manufacture).

## Main Workflow
SupplyBoost consists of several work flow. 

### Buy Products from Retailer

Customer view products on the Retail site and create ProductOrder for buying. If the Retailer has products in Stock,  
create an Invoice for the Delivery Company. Workflow has been finished when delivered products to Customer.

![Buy Product From Retail](resource/BuyProductFromRetail.svg)

**Hint: Each node have 4 parts.** 
  - Action
  - Entity 
  - Module (Microservice)
  - Description

# Getting Started

To install this application, run the following commands:

```bash
git clone https://github.com/saeidamini/SupplyBoost.git
mvn -Pprod verify com.google.cloud.tools:jib-maven-plugin:dockerBuild
cd docker-compose/
docker compose up
```
open browser navigate to [http://localhost:8080/](http://localhost:8080/). User and password is same : ``admin``.

# Technical Stack

SupplyBoost use the following open source libraries:

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Cloud](https://spring.io/projects/spring-cloud)
* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
* [Spring Security](https://spring.io/projects/spring-security)
* [Eureka](https://github.com/Netflix/eureka)
* [Hazelcast](https://hazelcast.com/)
* [Maven](https://maven.apache.org/)
* [Java 11+ : OpenJDK](https://openjdk.java.net/)
* [Docker](https://www.docker.com/)
* [Keycloak](https://www.keycloak.org/)


## License

Apache 2.0, see [LICENSE](LICENSE).
