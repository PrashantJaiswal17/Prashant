# Order Management Microservice (Spring Boot + Kafka)

This service implements an **event-driven Order Management** workflow designed for high throughput (targeting ~10k requests/sec with horizontal scaling).

## Architecture

- **Ingress API (sync):** `POST /api/v1/orders` accepts order create requests.
- **Idempotency key:** `requestId` ensures duplicate client retries do not create duplicate orders.
- **State store:** PostgreSQL stores order lifecycle and status.
- **Event backbone:** Kafka topic `order-created.v1` receives newly accepted orders.
- **Async processor:** Kafka consumer processes creation events and updates order status.
- **Event outbox stream:** Processed results are published to `order-processed.v1`.

## Throughput Strategy (10k RPS)

To reach sustained 10k RPS, deploy with:

1. Multiple service instances behind a load balancer.
2. Kafka topic partition count >= total consumer concurrency across instances.
3. Adequate JVM sizing (G1/ZGC), CPU pinning, and autoscaling.
4. PostgreSQL tuning: connection pool sizing, partitioning/sharding, and read replicas.
5. Async communication between microservices through Kafka to remove synchronous bottlenecks.
6. Observability using Actuator metrics and external Prometheus/Grafana.

## API

### Create order

```http
POST /api/v1/orders
Content-Type: application/json

{
  "requestId": "req-1001",
  "customerId": "cust-777",
  "amount": 149.99
}
```

### Get order

```http
GET /api/v1/orders/{orderId}
```

## Local Run

1. Start PostgreSQL and Kafka locally.
2. Update credentials in `src/main/resources/application.yml` if needed.
3. Run:

```bash
mvn spring-boot:run
```

## Build and test

```bash
mvn clean test
```
