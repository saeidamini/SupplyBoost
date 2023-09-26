# SupplyBoost
![Banner](resource/banner.jpg)
SupplyBoost is a supply chain management application.
As an aspiring software developer, I'm always reading about new technologies and architectures - microservices, event streaming, crazy cloud stuff. It gets my nerd juices flowing!

But when it comes time to build something in the real-world, I struggle to put all these concepts together. Most projects are too small and I end up hacking things the old-fashioned way.

I know many of you face the same dilemma - you kinda know this tech but haven't really built anything meaningful with it.

So I started SupplyBoost to fix that! It's an ambitious supply chain app that lets me throw every book I've read at it! Microservices, check! Serverless, check! It's a cool playground to go wild!

No more toy apps and todo lists. This is the real deal - a complex beast that helps me level up my skills as an architect and developer. Sure I'll make ton of mistakes on the way, but that's how we learn 💪

In the process, I hope to turn all these buzzwords into practical skills that can be applied to big real-world projects. No more hacky duct tape code!

So come join me on this journey! It's gonna be challenging but oh so rewarding. Let's turn theory into practice. Who's with me? 🚀

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
