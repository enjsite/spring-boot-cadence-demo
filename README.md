# Order Flow with Uber Cadence

This project demonstrates order processing using **Uber Cadence** with workflow orchestration, signals, and queries.

## Key Features

- **Workflow Orchestration**: Order workflow defined in `OrderWorkflowImpl`.
- **Signals**: Payment confirmation sent asynchronously via REST endpoint.
- **Queries**: Retrieve order status synchronously via REST.
- **Task Queue**: Workflow tasks are processed by `order-worker`.
- **Deterministic Logic**: Workflow execution uses `Workflow.await` for payment waiting.

## How to Run

1. Make sure **Docker** and **Docker Compose** are installed.
2. Build and start the services:

bash:
docker-compose up --build

3. Wait for Cassandra, Cadence server
4. Register the domain for Cadence server (via bash terminal)

docker exec -it cadence cadence --domain test-domain --address cadence:7933 domain register

5. and the Spring Boot app (order-app) to start.

4. Access Cadence Web UI: http://localhost:8088
 to view workflow execution and history.

5. Use REST API to interact with workflows:

Action	Endpoint
- Start Order	POST http://localhost:8080/order/{orderId}
- Confirm Payment	POST http://localhost:8080/order/{orderId}/paid
- Get Status	GET http://localhost:8080/order/{orderId}/status

## Example:
### Start order
curl -X POST http://localhost:8080/order/1

### Confirm payment after some delay
curl -X POST http://localhost:8080/order/1/paid

### Check workflow status
curl http://localhost:8080/order/1/status

## Workflow Behavior

1. When an order is started, the workflow enters a WAITING_FOR_PAYMENT state.
2. Payment confirmation is sent as a signal to the workflow.
3. The workflow updates its status to PAID and completes.
4. If payment is delayed or fails, workflow can handle timeouts or compensation logic (extendable).

## Why Cadence?

Cadence provides reliable, fault-tolerant workflow execution:
- **Keeps all local state of workflows even if workers crash.**
- **Ensures signals (like payment confirmation) are delivered reliably.**
- **Guarantees deterministic execution and correct retries for long-running business processes.**



