# Room Reservation System

## Description
REST API for managing room reservations built with Spring Boot using TDD.

**Business rules:**
1. No double-booking (overlap detection)
2. Cannot cancel a completed/already cancelled reservation
3. Price = hourly rate × hours, ADMIN gets 20% off, 3+ hours gets 10% off
4. Only owner or ADMIN can cancel a reservation
5. Duplicate reservation returns existing one (idempotence)

## How to run locally
Requirements: Java 17
```bash
git clone https://github.com/yourusername/reservations.git
cd reservations
./mvnw spring-boot:run
```
App runs on `http://localhost:8080`

H2 console available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## How to run tests
```bash
./mvnw verify
```
Coverage report generated at `target/site/jacoco/index.html`

## Architecture
3-layer architecture: `controller` → `service` → `repository`  
All business rules live in the service layer.  
Domain exceptions mapped to HTTP codes via `GlobalExceptionHandler`.

## Testing strategy
- **Unit tests** – test all 5 business rules in isolation using Mockito to mock repositories
- **Integration tests** – test full stack (controller→service→H2) using MockMvc
- **Coverage: 91% line, 83% branch** (visible in CI artifacts)

## CI/CD
GitHub Actions pipeline on every push: build → test → JaCoCo report uploaded as artifact.
