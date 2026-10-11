# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, but incrementally—not a big-bang rewrite.

Today the styles are mixed:
- Store (and partly Fulfillment) use Panache “active record”: entities extend PanacheEntity and callers use Store.listAll / findById / persist, sometimes from the REST layer and sometimes via StoreService.
- Product uses a thin PanacheRepository, but ProductResource still owns transactions and persistence calls inline.
- Warehouse uses a clearer hexagonal shape: domain model, DbWarehouse JPA entity, WarehouseStore port, WarehouseRepository adapter, and use cases that orchestrate validation + persistence.

For maintenance I would standardize on one rule per bounded context, not one pattern for the entire monolith:

1) Keep the Warehouse approach for anything with non-trivial invariants (creation/replace/archive, fulfillment rules). It keeps business logic testable without HTTP or Hibernate, and hides query details (e.g. “active only”, businessUnitCode as the external id) behind the port.

2) For Store and Product, either commit to “resource → service → repository/entity” everywhere (no Panache calls in resources) or accept active record only for truly simple CRUD—but pick one. Right now StoreResource reads directly from Store while writes go through StoreService, which makes behaviour and testing harder to reason about.

3) Refactor FulfillmentService first: it already depends on WarehouseStore and ProductRepository but still reaches into Store.findById and FulfillmentAssociation Panache queries. Aligning it with a small FulfillmentStore port (or at least a dedicated repository) would remove the worst coupling and match the warehouse module.

I would not refactor working Store/Product endpoints purely for aesthetics unless we are changing behaviour or adding features; the cost is regression risk for little gain on simple tables. The goal is consistency where complexity lives, not identical architecture everywhere.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
OpenAPI-first (Warehouse)
Pros:
- Contract is explicit and reviewable before implementation; good for consumer teams and versioning.
- Generated interfaces/DTOs reduce drift between spec and code (path params, response shapes, shared models).
- Swagger UI and client generation come “for free” if the spec stays the source of truth.
Cons:
- Build/plugin complexity (codegen, merge conflicts on generated sources).
- Spec changes require regenerating and adapting implementations; can feel heavy for small CRUD tweaks.
- Domain model may not match generated API types— you need mapping layers (as with WarehouseResponse vs domain Warehouse).

Hand-coded JAX-RS (Store, Product)
Pros:
- Fast to change; minimal ceremony for a small internal API.
- Easy to return JPA entities directly when JSON shape == entity (as Store/Product do today).
Cons:
- No single source of truth—documentation (if any) lags unless you maintain OpenAPI separately.
- Public API shape is coupled to persistence (entities leak fields, renames break clients).
- Harder to enforce consistency across services (status codes, error bodies, naming).

My choice for this codebase:
- Keep OpenAPI-first for Warehouse (and any future externally consumed or long-lived API): the rules and identifiers (businessUnitCode) benefit from a stable contract.
- For Store/Product, I would either (a) add lightweight OpenAPI when they become integration boundaries, or (b) keep hand-coded but introduce explicit DTOs + a shared error response pattern so we do not expose entities forever.

If the team owns one deployable monolith and only the UI calls these APIs, hand-coded is acceptable short term. If multiple clients or a public gateway appear, I would migrate Store/Product to spec-first the same way as Warehouse, ideally one OpenAPI file per bounded context rather than one giant spec.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Prioritize tests where bugs are expensive: business rules, transaction boundaries, and cross-system behaviour—not every line of boilerplate.

Priority order I would use here:

1) Domain / use-case unit tests (fast, no DB)
   - WarehouseValidator and Create/Replace/Archive use cases: these encode the assignment constraints (location, capacity, duplicate BU code, replace stock rules). Mockito on WarehouseStore/LocationResolver gives high value per minute.

2) Service-level tests with @QuarkusTest + @TestTransaction where rules touch the database
   - FulfillmentService (three association limits), StoreService (validation paths).
   - Focus on edge cases and failure responses (404/422), not happy-path duplication.

3) Thin HTTP smoke / contract tests
   - One RestAssured flow per resource for critical behaviour (e.g. StoreLegacySyncTest proving legacy sync runs after successful persist, warehouse create/archive via API).
   - Avoid re-testing every branch already covered in use-case tests.

4) De-prioritize or exclude from coverage gates
   - Generated OpenAPI stubs, pure DTOs/entities, and REST adapters that only delegate—cover them indirectly via API smokes.

Over time, keep coverage effective by:
- Enforcing a minimum line threshold in CI (e.g. JaCoCo on application logic, not generated code) on `./mvnw verify`, so regressions fail the build.
- Using quarkus-jacoco so @QuarkusTest coverage is included, plus unit tests for non-Quarkus code.
- Writing a test when fixing a bug (regression test) and when adding a new invariant.
- Reviewing coverage reports for uncovered branches in validators/use cases, not chasing 100% on resources.

This project’s mix—many Mockito tests for warehouse domain, targeted Quarkus tests for fulfillment/store legacy sync, and a small set of endpoint tests—is a reasonable balance. I would add warehouse API integration tests only for endpoints not fully exercised by use-case + one happy-path IT, rather than duplicating the entire validation matrix at HTTP level.
```
