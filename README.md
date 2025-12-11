# Spring Final Project — Booking Service (MVP)

This repository contains a microservice-based MVP for a cinema booking system, created as part of the Spring Framework final project.  
The system demonstrates REST API development with Spring Boot, PostgreSQL integration using Flyway, event-driven communication using Kafka, and full containerization using Docker Compose.

---

## Tech Stack

**Backend:**
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Flyway
- Apache Kafka

**Infrastructure:**
- PostgreSQL
- Docker & Docker Compose
- Swagger / OpenAPI

---

## Architecture Overview

The project consists of two microservices:

### booking-service
- Exposes REST API
- Persists data to PostgreSQL
- Publishes `BookingCreatedEvent` to Kafka

### notification-consumer
- Listens to Kafka topic `booking-events`
- Logs consumed events (simulated notifications)

---

## High-Level Data Flow

```
Client → booking-service (REST) → PostgreSQL
                                ↘
                                 Kafka topic "booking-events"
                                               ↘
                                        notification-consumer
```

---

## How to Run

### Requirements
- Docker
- Docker Compose

### Start All Services

Run from root directory:

```bash
docker compose up --build
```

This will:
- Start PostgreSQL
- Start Kafka & Zookeeper
- Build booking-service and notification-consumer
- Apply Flyway migrations
- Run both microservices

---

## Swagger UI

Once booking-service is running:

http://localhost:8080/swagger-ui/index.html  
or  
http://localhost:8080/swagger-ui.html

---

## 📡 API Endpoints

Base URL: `http://localhost:8080/api/bookings`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/bookings`        | Create a new booking |
| GET    | `/api/bookings`        | Get all bookings |
| GET    | `/api/bookings/{id}`   | Get booking by ID |
| DELETE | `/api/bookings/{id}`   | Delete a booking |
| GET    | `/api/bookings/health` | Health check |

---

## 🗄️ Database (Flyway)

Flyway migrations are stored in:

```
booking-service/src/main/resources/db/migration/
```

### Migration File: `V1__create_booking_table.sql`

```sql
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    movie_title        VARCHAR(255) NOT NULL,
    customer_name      VARCHAR(255) NOT NULL,
    customer_email     VARCHAR(255) NOT NULL,
    number_of_tickets  INT          NOT NULL,
    total_price        NUMERIC(10,2) NOT NULL,
    booking_time       TIMESTAMP    NOT NULL DEFAULT NOW(),
    status             VARCHAR(50)  NOT NULL
);
```

---

## 📂 Project Structure

```
Spring_final/
│
├── booking-service/
│   ├── src/main/java/.../controller
│   ├── src/main/java/.../service
│   ├── src/main/java/.../entity
│   ├── src/main/java/.../event
│   ├── src/main/resources/application.properties
│   └── src/main/resources/db/migration/V1__create_booking_table.sql
│
├── notification-consumer/
│   ├── src/main/java/.../consumer
│   ├── src/main/resources/application.properties
│
└── docker-compose.yml
```

---

## 🎧 Kafka Integration

### Producer (booking-service):
Publishes events to topic `booking-events`.

### Consumer (notification-consumer):
Reads events from the same topic and logs them.

This simulates an asynchronous notification system.

---

## 📝 Notes

This project demonstrates:
- Microservice architecture
- Clean REST API design
- PostgreSQL + Flyway database migration
- Kafka-based communication
- Dockerized deployment

It fully satisfies requirements for the Spring Final Project MVP.

---

## 👤 Authors

**Aubakirov Sanzhar**  
**Aziza Gilash**  

KBTU — Information Systems  
Spring Framework Final Project (2025)