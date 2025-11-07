This application provides a centralized platform for enterprise managers to administer service subscriptions for their members. It allows for dynamic pricing management, member administration, and provides a public-facing portal for members to view and select service tiers.



## Tools:

**Devops:**

* GitHub Actions
* SonarQube
* Docker
* Terraform
* Kubernetes
* OpenTelemetry
* Datadog
* AWS



**Compose Multiplatform Mobile/Web:**

* Ktor\_Client
* SQLDelight
* Koin
* Jetpack Compose



**Web Angular:**

* Angular Material
* HttpClient
* Jasmine
* Karma
* Angular Testing Library
* Cypress



**Backend:**

* Java Spring Boot
* Spring Data JPA Hibernate
* Spring Security
* Keycloak, OAuth2, OIDC, JWT
* Springdoc OpenAPI (for Swagger UI)
* Spring AMQP - RabbitMQ
* Kafka
* Spring Validation
* Testing:

  * Spring Test
  * JUnit
  * Mockito
  * Testcontainers
  * WireMock

* Spring Cloud:

  * Netflix Eureka Server (Registry)
  * Netflix Eureka Client
  * Gateway
  * Config

* Redis
* Flyway



## Domain: Diagram

* **member-service**: some Manager can CRUD Members
* **pricing-service**: have 3 choices (free, half price, full price), with value and description
* **service-app-registry** register all services in the network
* **service-app-infra** handles all the infra (docker-compose, databases, keycloak, rabbitmq, kafka, redis, etc)
* **service-app-gateway**: organize routes, auth, ratelimiter, load balancer
* **Landing-Feature**: show the 3 choices and description. Some AI (user send a text and AI help make decision). Some Video
* **Management-Feature**: A single dashboard for managers. It will have two tabs:
  * **Members Tab**: Provides full CRUD functionality for members.
  * **Prices Tab**: Edit-only for `value` and `description` of the 3 service tiers (no create/delete)
* **Auth-Feature**: Authentication flow



## Later:

* **notification-service**: send email to Member saying that the Service was confirmed
* Add translation for PT-br
* stress test
* create readme for each service, and some diagram (Excalidraw, C4)



## To-do:

**Current:**

* handle the images
* configure OTel and Datadog (check logs)
* use springdoc
