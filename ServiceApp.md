This application provides a centralized platform for enterprise managers to administer service subscriptions for their members. It allows for dynamic pricing management, member administration, and provides a public-facing portal for members to view and select service tiers.


## Tools:

**Devops:**
* Github Actions
* SonarQube
* Docker
* Kubernetes
* Metrics: Prometheus (collection) e Grafana (visualization)
* Logs: Elasticsearch, Fluentd/Logstash e Kibana
* Tracing: Jaeger
* Terraform + AWS (after finishing)


**CMP Mobile:**
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
* Spring Cloud:
    * Netflix Eureka Server (Registry)
    * Netflix Eureka Client
    * Gateway
    * Config
* Redis
* Flyway


## Domain: Diagram 

* Follow TDD and make a test for each commit/task (when applicable)
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
* Kafka to get Member service requests from landing page, and show in the member-service so the Manager can add
* **notification-service**: send email to Member saying that the Service was confirmed
* Add translation for PT-br
* create readme for each service, and some diagram


## To-do:
**Current:**
* send a price message to member-service using rabbitmq and cache it with redis
* get the prices in the member-feature and show
* Feature Landing, create 3 cards with the price and description
