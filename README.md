# Cinema Ticketing (Final Project — upgraded from MVP)

This repo is being upgraded step-by-step to match the **Final exam requirements**: microservices, REST + Kafka, DB + Flyway, Swagger, testing, and later Keycloak.

## Step 1 architecture (already implemented)

**event-service (catalog)**  → (REST) → **booking-service** → (Kafka `booking-events`) → **notification-service**  → (Kafka `notification-events`) → **booking-service**

### Flow
1) **event-service** stores events (movie, start time, price, available seats).
2) **booking-service** creates bookings **only after validating the event via REST**.
3) booking-service publishes Kafka event `BookingCreated` to topic **`booking-events`**.
4) **notification-service** (currently still named `notification-consumer`) consumes `booking-events`, simulates sending email, and publishes `NotificationSent` to topic **`notification-events`**.
5) booking-service consumes `notification-events` and updates booking status → `NOTIFIED`.

## Services & Ports
- `event-service` : **8082**
- `booking-service`: **8081**
- `notification-consumer` (notification-service): **8083**
- `kafka-ui`: **8089**
- Postgres: booking_db **5433**, event_db **5434**, notification_db **5435**

## Run (Docker)
```bash
docker compose up --build
```

## Demo flow
### 1) Create an event
```bash
curl -X POST http://localhost:8082/api/admin/events \
  -H 'Content-Type: application/json' \
  -d '{"movieTitle":"Interstellar","startTime":"2025-12-25T19:30:00","pricePerTicket":12.5,"availableSeats":50}'
```

### 2) List events
```bash
curl http://localhost:8082/public/events
```

### 3) Create a booking (use `eventId` from step 2)
```bash
curl -X POST http://localhost:8081/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"eventId":1,"customerName":"Aza","customerEmail":"aza@example.com","numberOfTickets":2}'
```

### 4) Check booking status
```bash
curl http://localhost:8081/api/bookings/1
```
After notification-service processes the Kafka event, the status should become **`NOTIFIED`**.

## Swagger
- booking-service: http://localhost:8081/swagger-ui.html
- event-service: http://localhost:8082/swagger-ui.html
- notification-service: http://localhost:8083/swagger-ui.html

> Next steps: DB + Flyway for notification-service, Keycloak roles, PUT/PATCH, and full test suite.
