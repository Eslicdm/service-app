# Club Membership Management Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-Material-red.svg)](https://angular.io/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-blue.svg)](https://kubernetes.io/)

## 📖 Overview

This application is a comprehensive platform designed for managing an exclusive recreational club. It facilitates dynamic pricing for membership tiers (Garden, Club, Patron), member administration, and provides a public-facing portal where prospective members can view benefits and receive AI-driven recommendations. The system features role-based access control, directing Managers to the administration dashboard and Members to social features like the exclusive chat.

## 🚀 Key Features

*   **Role-Based Access:** Secure routing based on user roles—Managers access the Dashboard, while Members access the Chat and Portal.
*   **Management Dashboard:** A centralized hub for managers to administer club members (CRUD) and adjust membership pricing tiers.
*   **Public Portal:** A user-friendly landing page displaying membership choices (Garden Pass, Club Membership, Patron Membership).
*   **AI Recommendation Assistant:** Uses **Spring AI (RAG + Google GenAI)** to help users select the best plan based on natural language input.
*   **Dynamic Pricing:** Real-time updates to service descriptions and values.
*   **Secure Authentication:** Robust auth flow using Keycloak, OAuth2, and OIDC.

## 🏗 Architecture & Microservices

The system is built on a Microservices architecture using **Spring Cloud**.

| Service Name | Description |
| :--- | :--- |
| **`service-app-gateway`** | Entry point handling routing, authentication, rate limiting, and load balancing. |
| **`service-app-registry`** | Netflix Eureka Server for service discovery. |
| **`service-app-infra`** | Manages infrastructure configurations (K8s, Docker Compose). |
| **`member-service`** | Handles member data persistence and CRUD operations. |
| **`pricing-service`** | Manages the 3 pricing tiers (Free, Half, Full). |
| **`member-request-service`** | Ingests member requests and forwards them to the member service. |
| **`recommendation-service`** | **AI-powered** service assisting users in plan selection. |

## 🛠 Tech Stack

### Backend
*   **Core:** Java 21, Spring Boot, Spring Data JPA (Hibernate), Spring Validation
*   **Messaging:** Spring AMQP (RabbitMQ), Spring Kafka
*   **Security:** Spring Security, Keycloak, OAuth2, OIDC, JWT
*   **AI:** Spring AI (ChatClient, RAG, Google GenAI), Weaviate (Vector DB)
*   **Cloud & Discovery:** Spring Cloud (Eureka, Gateway, Config, Kubernetes)
*   **Database & Migrations:** Redis, Flyway
*   **Testing:** JUnit, Mockito, Testcontainers, WireMock

### [Frontend (Web)](https://github.com/Eslicdm/service-app-angular)
*   **Framework:** Angular
*   **UI:** Angular Material, Tailwind
*   **Testing:** Jasmine, Karma, Angular Testing Library, Cypress

### [Frontend (Multiplatform - Mobile/Web/Desktop)](https://github.com/Eslicdm/ServiceAppCompose)
*   **Framework:** Jetpack Compose Multiplatform
*   **Libraries:** Ktor Client, SQLDelight, Koin

### DevOps & Infrastructure
*   **Containerization:** Docker, Kubernetes
*   **CI/CD:** GitHub Actions, SonarQube
*   **Observability:** OpenTelemetry, Datadog

## 🐳 How to Run with Docker Compose

This project uses Docker Compose to orchestrate the microservices, databases, and infrastructure components (Keycloak, Kafka, Redis, etc.).

### 1. Prerequisites

*   **Docker Desktop** installed and running.
*   **Java 21** (for building the JARs if you run builds locally, though the Dockerfile handles the build).

### 2. Configuration Setup

#### A. Environment Variables (`.env`)
The `docker-compose.yml` relies heavily on environment variables. Create a file named `.env` in the same directory as your `docker-compose.yml` and populate it with `.env.example`


#### B. Hosts File Configuration
To ensure Keycloak operates correctly with the frontend and other services, you must map the hostname `keycloak` to your local machine.

*   **Windows (PowerShell as Admin):**
    ```powershell
    Add-Content -Path C:\Windows\System32\drivers\etc\hosts -Value "`n127.0.0.1 keycloak"
    ```
*   **macOS / Linux:**
    ```bash
    sudo echo "127.0.0.1 keycloak" >> /etc/hosts
    ```

### 3. Startup

Run the following command to build the images and start the containers. Note that the build context involves the parent directories (`../..`), so this might take a few minutes the first time.

```bash
docker-compose up -d --build
```
