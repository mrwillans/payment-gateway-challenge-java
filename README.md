# Payment Gateway Challenge — Java

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker (for the bank simulator)


## Running the application

### 1. Start the bank simulator

The application depends on a bank simulator running at `http://localhost:8080`. Start it with:

```bash
docker-compose up
```

### 2. Start the application

```bash
./gradlew bootRun
```

The gateway will be available at **http://localhost:8090**.

## Running the tests

> **Note:** The integration tests in `PaymentGatewayControllerTest` make HTTP calls to the
> bank simulator. Ensure `docker-compose up` is running before executing the full test suite.

Run all tests:

```bash
./gradlew test
```


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

## Implementation details

- **Supported currencies:** Validated against exactly three ISO codes — `USD`, `EUR`, and `GBP`
- **Rejected payments:** When request validation fails, the gateway returns `HTTP 400` with
a response body containing `status: "Rejected"`. The acquiring bank is never called in this case.
- **503 from bank:** If the bank simulator returns a `503 Service Unavailable` (card number 
ending in `0`), this is propagated directly to the caller as a `503` response.
The payment is not stored.
- **Card number masking:** Only the last four digits of the card number are
stored and returned. The full card number is never persisted.
- **Payment ID:** Each processed payment is assigned a random UUID as its identifier,
returned in the response and usable for subsequent retrieval.