# SupplyBoost

SupplyBoost is a supply chain management application with Gateway and five microservice applications. This uses eureka for service discovery and JWT authentication.

![Supply Chain Process](resource/SupplyChainProcess.drawio.svg)

## SupplyBoost's modules

SupplyBoost have these modules:
- **Supply**
    - They receive the requests and send them to the factories (use by Supplier).
- **Manufacture**
    - They Plan for Production (use by Manufactury).
- **Distribute**
    - The Goods are Delivered by the Retailer (use by Distributor).
- **Retail**
    - In Stores, they sales Goods to Customer (use by Retailer).
- **Notification**
  - Send all type of notification(SMS, Email, ...) in other modules.


## Technical Stack

SupplyBoost use the following open source libraries:

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Cloud](https://spring.io/projects/spring-cloud)
* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
* [Spring Security](https://spring.io/projects/spring-security)
* [Consul](https://github.com/hashicorp/consul)
* [Hazelcast](https://hazelcast.com/)
* [Maven](https://maven.apache.org/)
* [Java 11+ : OpenJDK](https://openjdk.java.net/)
* [Docker](https://www.docker.com)


## License

Apache 2.0, see [LICENSE](LICENSE).
