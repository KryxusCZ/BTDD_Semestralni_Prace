# Rezervační systém – BTDD & DevOps

**Repozitář:** https://github.com/KryxusCZ/BTDD_Semestralni_Prace

REST API pro správu rezervací místností. Projekt pokrývá dva předměty:
- **BTDD** – vývoj aplikace metodologií TDD (Spring Boot, Java 17)
- **DevOps** – CI/CD pipeline, kontejnerizace, Kubernetes nasazení

---

## BTDD – Test-Driven Development

### Business pravidla

1. Žádné překrývající se rezervace (detekce kolizí)
2. Nelze zrušit dokončenou nebo již zrušenou rezervaci
3. Cena = hodinová sazba × počet hodin; ADMIN má slevu 20%, rezervace 4+ hodiny mají dalších 10% slevu
4. Rezervaci může zrušit pouze její vlastník nebo ADMIN
5. Duplicitní rezervace vrátí existující (idempotence)

### TDD metodologie

Každé business pravidlo bylo implementováno v cyklu **red → green → refactor**, viditelné v git historii (`git log --oneline`).

### Architektura

3-vrstvá: `controller` → `service` → `repository`

- Business logika je výhradně ve vrstvě `service`
- Doménové výjimky jsou mapovány na HTTP kódy přes `GlobalExceptionHandler`
- Testy: H2 in-memory databáze (dev/test), PostgreSQL (prod)

### Testovací strategie

| Typ | Nástroj | Co testuje |
|---|---|---|
| Jednotkové testy | JUnit 5 + Mockito | 5 business pravidel izolovaně, repository mockováno |
| Integrační testy | MockMvc + H2 | Celý stack controller → service → databáze |
| Pokrytí kódu | JaCoCo | 91 % řádků, 83 % větví |
| Statická analýza | Checkstyle | Dodržení coding standards |

### Lokální spuštění (H2, bez DB serveru)

```bash
git clone https://github.com/KryxusCZ/BTDD_Semestralni_Prace.git
cd BTDD_Semestralni_Prace
./mvnw spring-boot:run
```

- API: `http://localhost:8080/actuator/health`
- H2 konzole: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, heslo: prázdné)

### Spuštění testů

```bash
./mvnw verify
```

- Coverage: `target/site/jacoco/index.html`
- Test results: `target/surefire-reports/`

---

## DevOps

Podrobná dokumentace: [`README-DEVOPS.md`](README-DEVOPS.md)

### Přehled

| Komponenta | Technologie |
|---|---|
| REST API | Spring Boot 4, Java 17 |
| Databáze | PostgreSQL 16 (prod/staging), H2 (testy) |
| Kontejnery | Docker (multi-stage build) |
| Orchestrace | Kubernetes (minikube) |
| CI/CD | GitHub Actions |
| Registry | GitHub Container Registry (ghcr.io) |

### CI/CD pipeline

- **CI** – spouští se při každém `push` a `pull_request`: Maven build, unit + integrační testy, Checkstyle, JaCoCo, Docker build + push do GHCR
- **CD** – spouští se automaticky po úspěšném CI: nasazení do `reservations-staging`; produkce (`reservations-prod`) pouze přes `workflow_dispatch`

### Kubernetes

Dva namespacy v minikube:

| | Staging | Production |
|---|---|---|
| Namespace | `reservations-staging` | `reservations-prod` |
| Repliky | 1 | 2 |
| Storage DB | `emptyDir` | `PersistentVolumeClaim` (1Gi) |

### Rychlý start (K8s)

```bash
minikube start --driver=docker
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/staging/
kubectl port-forward svc/reservations 8080:80 -n reservations-staging
```

API dostupné na `http://localhost:8080/actuator/health`
