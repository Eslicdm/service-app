This application provides a centralized platform for managing a recreational club. It allows for dynamic pricing management of membership tiers, member administration, and provides a public-facing portal for users to view and select membership options. The system distinguishes between Managers and Members, providing tailored interfaces for each (Dashboard vs. Chat).



## Tools:

**Devops:**

* GitHub Actions
* SonarQube
* Docker
* Kubernetes
* OpenTelemetry
* Datadog
* Terraform
* AWS



**Compose Multiplatform Mobile/Web/Desktop:**

* Ktor\_Client
* SQLDelight
* Koin
* Jetpack Compose



**Web Angular:**

* Angular Material
* Tailwind
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
* Spring Kafka
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
  * Kubernetes

* Redis
* Flyway
* Spring AI (ChatClient, RAG, Google GenAI, VectorDatabase (Weaviate))



## Domain:

* **member-service**: some Manager can CRUD Members
* **pricing-service**: have 3 choices (Garden, Club, Patron), with value and description
* **member-request-service**: receive member request and send to member-service
* **recommendation-service**: helps users choose a service plan using an AI assistant.
* **service-app-registry** register all services in the network
* **service-app-infra** handles all the infra (k8s, docker-compose, etc.)
* **service-app-gateway**: organize routes, auth, ratelimiter, load balancer
* **Landing-Feature**: show the 3 choices and description. Some AI (user sends a text and AI helps make a decision)
* **Management-Feature**: A single dashboard for managers. It will have two tabs:
  * **Members Tab**: Provides full CRUD functionality for members.
  * **Prices Tab**: Edit-only for `value` and `description` of the 3 service tiers (no create/delete)
* **Chat-Feature**: A real-time chat interface for authenticated members (Role: Member) to interact.
* **Auth-Feature**: Authentication flow handling Roles (Manager/Member).



## To-do:

* **notification-service**: send email to Member saying that the Service was confirmed
* * **Chat-Feature**: a chat for authenticated members talk with each other
* Add translation for PT-br
* stress test
* create readme for each service, and some diagram (Excalidraw, C4)
* make ci/cd with github actions when using AWS and Terraform
* add some video in the landing-feature
* create figma layout



## In Progress:


* create a k8s and docker for recommendation-service
* adjust the CMP
* add integration test in angular
