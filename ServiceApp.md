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
* Keycloak, OAuth2, JWT
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
* **pricing-service**: have 3 choices (free, half price, full price), and can edit their price and text description. And can see how many Members selected some choice
* **service-app-registry** register all services in the network
* **service-app-infra** handles all the infra (docker-compose, databases, keycloak, rabbitmq, kafka, redis, etc)
* **service-app-gateway**: organize routes, auth, ratelimiter, load balancer
* **Landing-Feature**: show the 3 choices and description. Some AI (user send a text and AI help make decision). Some Video
* **Member-Feature**: show the Member list and CRUD
* **Pricing-Feature**: show the prices and CRUD
* **Auth-Feature**: make the auth flow



## Later:

* **notification-service**: send email to Member saying that the Service was confirmed
* Add translation for PT-br
* stress test
* create readme for each service, and some diagram (Excalidraw, C4)



## To-do:

**Current:**

* see the kotlin in the servicetype enum
* integrate all servies to eureka and test the gateway
* configure OTel and Datadog
* remove the css and use Tailwind4
* change reative gateway to use virtual threads
* configure kafka to receive the request from the landing page
* change the keycloak login to auth0
* use springdoc
