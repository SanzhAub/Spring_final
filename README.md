# Spring Cinema Microservices (Final Project)

> **Microservice-based cinema booking system** built with Spring Boot, PostgreSQL, Kafka and an API Gateway.  
> Designed to demonstrate clean service boundaries, database migrations, async event flow, and containerized local setup.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [How to Run Locally (Docker)](#how-to-run-locally-docker)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Requirements Coverage](#project-requirements-coverage)

---

## Project Overview

This project models a simple **cinema booking** flow:

1. **Event Service** manages movie sessions (`Event`) and their seats (`Seat`).
2. **Booking Service** creates bookings and interacts with Event Service (HTTP) to reserve/release seats.
3. **Kafka** is used to publish booking events for asynchronous processing (e.g., notification workflow).
4. **API Gateway** provides a single entrypoint and routes requests to services.
5. **PostgreSQL + Flyway** are used per-service for persistent storage and migrations.
6. **Keycloak** is included in the docker setup (security wiring can be enabled/extended as needed).

---

## Architecture

```
                +------------------+
                |   API Gateway    |
                |     :8083        |
                +--------+---------+
                         |
          +--------------+----------------+
          |                               |
+---------v----------+            +-------v-----------+
|   Booking Service  |            |   Event Service   |
|      :8082         |            |      :8081        |
|  - JPA + Flyway    |  HTTP      |  - JPA + Flyway   |
|  - Feign/Rest call +----------->|  - Seat mgmt      |
|  - Publishes Kafka |            |                   |
+---------+----------+            +---------+---------+
          |                                 
          | Kafka (booking events)                  
+---------v----------+                 
|      Kafka         |  + Kafka UI :8080
+--------------------+

Databases:
- PostgreSQL container `postgres` (per-service schemas/DBs via configuration)
Auth:
- Keycloak container `keycloak` :8084
```

---

## Services

| Service | Folder | Port (default) | Responsibility |
|---|---|---:|---|
| API Gateway | `api-gateway/` | **8083** | Single entrypoint, routes requests to backend services |
| Event Service | `event-service/` | **8081** | Events (movie sessions), seats, reservation logic |
| Booking Service | `booking-service/` | **8082** | Booking lifecycle, communicates with Event Service, publishes booking events |
| Kafka UI | (docker) | **8080** | Inspect topics/messages locally |
| Keycloak | (docker) | **8084** | Auth server (included; can be extended) |
| PostgreSQL | (docker) | **5432** | Persistence for services |
| Kafka | (docker) | **9092** | Event streaming backbone |

---

## Tech Stack

- **Java 17** (recommended for consistent builds)
- **Spring Boot** (services)
- **Spring Data JPA + Hibernate**
- **Flyway** database migrations
- **PostgreSQL**
- **Apache Kafka**
- **Spring Cloud Gateway**
- **Swagger / OpenAPI** (where enabled)
- **JUnit 5 + Mockito** (unit tests)
- **Docker + docker-compose** for local infrastructure

---

## How to Run Locally (Docker)

### 1) Start infrastructure + services
From repo root:

```bash
docker compose up --build
```

This will start:
- postgres, kafka, kafka-ui, keycloak
- booking-service, event-service, api-gateway

### 2) Verify containers
```bash
docker ps
```

### 3) Useful URLs
- **API Gateway**: `http://localhost:8083`
- **Booking Service**: `http://localhost:8082`
- **Event Service**: `http://localhost:8081`
- **Kafka UI**: `http://localhost:8080`
- **Keycloak**: `http://localhost:8084`

---

## Configuration

Service configuration is stored in:
- `booking-service/src/main/resources/application.properties`
- `event-service/src/main/resources/application.properties`
- `api-gateway/src/main/resources/application.properties`

Docker infra is defined in:
- `docker-compose.yml`

Key values:
- PostgreSQL host: `postgres`
- Kafka broker: `kafka:9092`

---

## API Documentation

### Event Service
- Manages movie sessions and seats.
- Base path: `/events`

### Booking Service
- Creates bookings and triggers seat reservation.
- Base path: `/bookings`

> If Swagger/OpenAPI is enabled in a service, you can typically access it at:
- `http://localhost:<port>/swagger-ui.html` or `/swagger-ui/index.html`

---

## Testing

### Run tests per service

**Event Service**
```bash
cd event-service
mvn -q clean test
```

**Booking Service**
```bash
cd booking-service
mvn -q clean test
```

**API Gateway**
```bash
cd api-gateway
mvn -q clean test
```

### “Proof” output for defense (echo + exit code)
A simple pattern you can use in the terminal during defense:

```bash
mvn -q clean test && echo "✅ TESTS PASSED"
```

Or to show exit code explicitly:

```bash
mvn -q clean test; echo "exit_code=$?"
```

---

## Project Requirements Coverage

This README is aligned to the provided requirements document and highlights the key implemented parts:

-  **Microservices**: separate `booking-service`, `event-service`, plus `api-gateway`
-  **Database + ORM**: Spring Data JPA + Hibernate with PostgreSQL
-  **Migrations**: Flyway included for schema versioning
-  **Kafka**: broker + UI and service-level integration hooks
-  **Dockerized local setup**: single `docker-compose.yml`
-  **Testing**: unit/integration tests runnable via Maven

> **Note:** Keycloak is included in the docker infrastructure. Security configs can be tightened/enabled (e.g., JWT resource server + roles) depending on the defense checklist and time.

---

## Authors
Team project for the **Spring Framework Final** course.
