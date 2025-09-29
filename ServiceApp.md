A generic application for some Enterprise manager Services





###### Tools:

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
* Spring Data JPA e Hibernate
* Spring Security
* Keycloak, OAuth2, JWT
* Springdoc OpenAPI
* Swagger
* Spring AMQP - RabbitMQ
* Spring Validation
* Spring Test
* TestContainers
* JUnit
* Mockito
* Spring Cloud Gateway
* Spring Cloud Config
* Redis
* Flyway





###### Domain:

* Follow TDD and make test for each commit/task (when aplicable)
* **member-service**: some Manager can CRUD Members
* **pricing-service**: have 3 choices (free, half price, full price), and can edit their price and text description. And can see how many Members selected some choice
* **notification-service (later)**: send email to Member saying that the Service was confirmed
* **Landing-Page**: show the 3 choices and description. Some AI (user send a text and AI help make decision). Some Video
* **service-app-gateway**: organize routes, auth,
* Add translation for PT-br



Kafka to get Member service requests from landing page, and show in the member-service so the Manager can add





###### To-do:

**Current:**

* configure api gateway
* make the IT test in pricing-service
* analize pricing-service test
* Feature Landing, create 3 cards with the price and description
* after pricing with rabbitmq and landing with kafka make deploy in render (or another)
* create readme for pricing-service, member-service, angular





**Devops:**





**CMP Mobile:**

Make CMP after the application is working





**Web Angular:**

* Feature Landing: {
* create a button to login
* 3 cards to show the price choices,
* a text to AI decision,
* a video
* }
* Feature Auth: {
* make login with keycloak and navigate to Member (check)
* 
* }
* Feature Member: {
* create a member-service connection to backend to get the member list of some manager
* }
* Feature Manager:





**Backend Java:**

* pricing-service:
* member-service:
* notification-service:
* manager-service:
