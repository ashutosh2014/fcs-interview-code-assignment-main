# Fulfilment & Warehouse Code Assignment

Monorepo for a **warehouse colocation / fulfilment** case study: a Quarkus application that manages **locations**, **stores**, **products**, **warehouses**, and **store–product–warehouse** fulfilment links, plus written case-study and design notes.

The runnable application lives in [`java-assignment/`](java-assignment/). Everything else supports the assignment, CI, and discussion.

---

## Repository layout

| Path | Purpose |
| --- | --- |
| [`java-assignment/`](java-assignment/) | Quarkus 3 app (Java 17), tests, OpenAPI, UI |
| [`java-assignment/CODE_ASSIGNMENT.md`](java-assignment/CODE_ASSIGNMENT.md) | Implementation tasks (must-have + bonus) |
| [`java-assignment/QUESTIONS.md`](java-assignment/QUESTIONS.md) | Written answers on architecture and testing |
| [`case-study/BRIEFING.md`](case-study/BRIEFING.md) | Domain overview (entities, replace flow) |
| [`case-study/CASE_STUDY.md`](case-study/CASE_STUDY.md) | Cost-control scenarios (discussion) |
| [`.github/workflows/ci.yml`](.github/workflows/ci.yml) | GitHub Actions: tests + JaCoCo ≥ 80% |

---

## Domain (short)

- **Location** — city / area with capacity and warehouse limits  
- **Store** — retail unit; synced to a **legacy** system after DB commit  
- **Warehouse** — identified by **business unit code**; supports create, archive, and **replace** (archive old, reuse same code)  
- **Product** — catalogue items  
- **Fulfilment** — links which warehouses fulfil which products for which stores (with association limits)

Details: [`case-study/BRIEFING.md`](case-study/BRIEFING.md).

---

## Quick start

**Requirements:** JDK 17+, [Docker](https://docs.docker.com/get-docker/) (PostgreSQL via Quarkus Dev Services in dev/test).

```sh
cd java-assignment
chmod +x mvnw

# Dev mode (hot reload + Dev Services DB)
./mvnw quarkus:dev
```

Then open:

- UI: [http://localhost:8080/index.html](http://localhost:8080/index.html)  
- Swagger UI: [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

Build and run tests (including coverage gate):

```sh
cd java-assignment
./mvnw clean verify
```

JaCoCo HTML report: `java-assignment/target/site/jacoco/index.html`.

More detail: [`java-assignment/README.md`](java-assignment/README.md).

---

## HTTP API (overview)

| Area | Base path | Notes |
| --- | --- | --- |
| Stores | `/store` | CRUD; legacy sync via events after commit |
| Products | `/product` | CRUD |
| Warehouses | `/warehouse` | OpenAPI-generated contract; BU code as id |
| Fulfilment | `/fulfillment` | Associate / list / delete links |

Warehouse API spec: [`java-assignment/src/main/resources/openapi/warehouse-openapi.yaml`](java-assignment/src/main/resources/openapi/warehouse-openapi.yaml).

---

## Architecture highlights

Implementation follows **hexagonal-style** boundaries where complexity is highest:

- **Warehouses** — `domain` (models, validators, use cases, ports) + `adapters` (JPA, REST implementing generated OpenAPI)  
- **Fulfilment** — same pattern: `FulfillmentValidator`, use cases, `FulfillmentStore`, REST/database/catalog adapters  
- **Stores** — `StoreService` fires CDI events; `StoreLegacySyncObserver` calls `LegacyStoreManagerGateway` on `TransactionPhase.AFTER_SUCCESS` so legacy only sees committed data  
- **Location** — `LocationGateway.resolveByIdentifier` for warehouse location validation  

---

## Continuous integration

On push/PR to `main` or `master`, GitHub Actions:

1. Runs `./mvnw -B verify` in `java-assignment`  
2. Enforces **≥ 80%** line coverage (JaCoCo; Quarkus + unit tests)  
3. Publishes a **coverage summary** on the workflow run and uploads the HTML report as an artifact  

---

## Assignment checklist

- [ ] [`CODE_ASSIGNMENT.md`](java-assignment/CODE_ASSIGNMENT.md) — location, store legacy sync, warehouse, bonus fulfilment  
- [ ] [`QUESTIONS.md`](java-assignment/QUESTIONS.md) — design rationale  
- [ ] [`CASE_STUDY.md`](case-study/CASE_STUDY.md) — cost scenarios (optional discussion)

---

## Credits

Application code is based on [Quarkus quickstarts](https://github.com/quarkusio/quarkus-quickstarts). See [LICENSE](LICENSE) and [CONTRIBUTING.md](CONTRIBUTING.md).
