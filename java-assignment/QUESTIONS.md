# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. 
   If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I will not do a single rewrite, firstly I analyze the arhitecture, then decide the scope of changes task by task like a sprint work with verifying the changes with QAs with PI for regression.

Today implementation is mixed:
- Store use Panache: entities extend PanacheEntity and callers use Store.listAll / findById / persist 
    sometimes from the REST layer and sometimes via StoreService.
- Product uses a PanacheRepository, but ProductResource still owns transactions and persistence calls inline.
- Warehouse uses a clearer hexagonal architecure: domain i.e. model, DbWarehouse JPA entity, WarehouseStore port, WarehouseRepository adapter, and use cases.

For maintenance I would standardize on one rule per bounded context, not one pattern for the entire monolith:
- Keep the Warehouse approach as it as.
- For Store and Product, either commit to (resource → service → repository/entity) everywhere (no Panache calls in resources) or
  accept active record only for truly simple CRUD but pick one. 
  Right now StoreResource reads directly from Store while writes go through StoreService, which makes behaviour and testing harder to reason about.


I will not refactor Store/Product endpoints purely to match architecture unless we are changing behaviour or adding features; 
the cost is regression risk for little gain on simple tables. 

The goal is consistency where complexity lives, not identical architecture everywhere.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API
   from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. 
   What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
OpenAPI-first (Warehouse)
Pros:
- Doc is explicit and reviewable before implementation; good for consuming by other teams and help in versioning as well.
- Changes to the endpoints, req/res schemas, and status codes can easily reviewed for backward compatibility before implementation.
- Swagger UI gives the source of truth.
Cons:
- It increasae build complexity and maintenance.
- Spec changes require regenerating and adapting implementations; can feel heavy for small changes.

Hand-coded Store, Product)
Pros:
- Easy to change; minimal back-forth for a small internal change.
- Easy to return JPA entities directly when JSON model == entity (as Store/Product do today).

Cons:
- No single source of truth—documentation (if any) lags unless you maintain OpenAPI separately.
- Public API shape is coupled to persistence.
- Harder to enforce consistency like errors, status code, repsones.

My choice for this codebase:
- Keep OpenAPI-first for Warehous, the rules and identifiers (businessUnitCode) benefit from a stable contract.
- For Store/Product, I would either 
      - Add lightweight OpenAPI when they become integration boundaries.
      - keep hand-coded but introduce explicit DTOs + a shared error response pattern so we do not expose entities forever.

If team have monolith arch and only the UI calls these APIs, hand-coded is acceptable for short term. 
If multiple clients/public gateway appear/microservices, I would migrate Store/Product to spec-first the same way as Warehouse, 
ideally one OpenAPI file per bounded context rather than one giant spec like i have already generated to test and look for all my apis
```
----
3. Given the need to balance thorough testing with time and resource constraints, 
   how would you prioritize and implement tests for this project? 
   Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I will prioritize tests where bugs can easily occur: business rules (service layer commonly/Domain/User Case), transaction boundaries, 
 and cross-system behaviour, for not every line of boilerplate.

1) Domain / use-case unit tests
   - WarehouseValidator and Create/Replace/Archive use cases: these encode the assignment constraints (location, capacity, duplicate BU code, replace stock rules). 
     Mockito on WarehouseStore/LocationResolver gives high value per minute.

2) Service-level tests with @QuarkusTest + @TestTransaction where rules touch the database
   - StoreService will focus on edge cases and failure responses (404/422), not happy-path duplication.

3) Thin HTTP smoke / contract tests
   - One RestAssured flow per resource for critical behaviour testing.

4) De-prioritize or exclude from coverage gates
   - Generated OpenAPI stubs, pure DTOs/entities.

Over time, keep coverage effective by:
- Enforcing a minimum line threshold in CI (e.g. JaCoCo on application logic, not generated code) on `./mvnw verify`, so regressions fail the build.
- Using quarkus-jacoco so @QuarkusTest coverage is included, plus unit tests for non-Quarkus code.
- Writing a test when fixing a bug (regression test) and when adding a new invariant.
- Reviewing coverage reports for uncovered branches in validators/use cases, not chasing 100% on resources.

```
