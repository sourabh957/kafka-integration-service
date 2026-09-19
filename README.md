# Kafka Integration Service

A small Spring Boot service that demonstrates producing and consuming JSON events with Apache Kafka. The repository contains the application and a separate local Kafka environment:

```text
kafka-integration-service/
|-- kafka-infrastructure/   # Kafka and Kafka UI, managed with Docker Compose
|-- src/                    # Spring Boot producer, consumer, and REST API
|-- pom.xml
`-- README.md
```

The two processes have independent lifecycles. Start Kafka first, then start the service. Stopping the service does not stop Kafka, and stopping Kafka is done explicitly with Docker Compose.

## How it works

1. `POST /api/v1/messages` accepts an `OrderCreatedEvent`.
2. `MessageProducer` publishes the event to `kafka-topic-1` using the supplied key.
3. `MessageConsumer` reads the event as part of the `message-processing-group` consumer group.

The Spring Boot service connects to `localhost:9092` by default. Kafka UI connects through the Docker network at `kafka:29092`.

## Prerequisites

- Java 21
- Docker with Docker Compose v2
- Ports `8080`, `8081`, and `9092` available locally

The Maven wrapper is included, so a separate Maven installation is not required.

## Run locally

All commands below are run from the repository root.

### 1. Start Kafka infrastructure

```bash
docker compose -f kafka-infrastructure/docker-compose.yml up -d
docker compose -f kafka-infrastructure/docker-compose.yml ps
```

Kafka listens on `localhost:9092`. Kafka UI is available at [http://localhost:8080](http://localhost:8080).

### 2. Start the service

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8081`.

### 3. Publish an event

PowerShell:

```powershell
$body = @{
  eventId = "event-1001"
  eventType = "ORDER_CREATED"
  orderId = "order-1001"
  customerId = "customer-42"
  amount = 1499.00
  timestamp = "2026-09-19T10:00:00Z"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/messages?key=order-1001" `
  -ContentType "application/json" `
  -Body $body
```

Bash:

```bash
curl --request POST 'http://localhost:8081/api/v1/messages?key=order-1001' \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "event-1001",
    "eventType": "ORDER_CREATED",
    "orderId": "order-1001",
    "customerId": "customer-42",
    "amount": 1499.00,
    "timestamp": "2026-09-19T10:00:00Z"
  }'
```

The API responds with `Message sent to Kafka topic`, and the service log prints the consumed event. You can also inspect the topic and message in Kafka UI.

Alternatively, import `kafka-integration-service.postman_collection.json` into Postman and run **Publish Order Created Event**. The collection provides configurable `baseUrl` and `messageKey` variables and verifies the successful response.

## Stop components independently

Stop the Spring Boot service with `Ctrl+C`. Kafka continues running.

Stop Kafka and Kafka UI separately:

```bash
docker compose -f kafka-infrastructure/docker-compose.yml down
```

Add `-v` only when you intentionally want to delete Kafka's local Docker volumes.

## Configuration

Override the Kafka address without changing source code:

```powershell
$env:KAFKA_BOOTSTRAP_SERVERS = "kafka.example.internal:9092"
.\mvnw.cmd spring-boot:run
```

| Setting | Default | Purpose |
| --- | --- | --- |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address used by the service |
| `server.port` | `8081` | HTTP API port |
| Topic | `kafka-topic-1` | Topic used by the producer and consumer |
| Consumer group | `message-processing-group` | Consumer group used by the listener |

## Test

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS or Linux:

```bash
./mvnw test
```

## Troubleshooting

- If the service cannot connect, confirm the Kafka container is healthy with `docker compose -f kafka-infrastructure/docker-compose.yml ps`.
- If a port is already in use, stop the conflicting process or update the relevant port mapping and application configuration.
- If Kafka was started with an older incompatible configuration, run `docker compose -f kafka-infrastructure/docker-compose.yml down` and start it again.
