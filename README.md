# Ledger API

An in-memory ledger API built with Spring Boot.

## Requirements

- Java 25 ([SDKman](https://sdkman.io/) recommended for managing versions)
- Maven (wrapper included — no local installation needed)

## Quickstart

```bash
make run
```

- The server starts on `http://localhost:8080`.
- Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

Run `make help` to see all available targets.

## Try it out!

Full API reference at `http://localhost:8080/swagger-ui.html`. These commands walk through the full happy path — run them in order.

```bash
# create a ledger and capture the ID
ID=$(curl -s -X POST http://localhost:8080/ledgers \
  -H 'Content-Type: application/json' \
  -d '{"name":"savings"}' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

# deposit into ledger
curl -s -X POST http://localhost:8080/ledgers/$ID \
  -H 'Content-Type: application/json' \
  -d '{"type":"DEPOSIT","amount":"500.00"}' | jq

# withdraw from ledger
curl -s -X POST http://localhost:8080/ledgers/$ID \
  -H 'Content-Type: application/json' \
  -d '{"type":"WITHDRAWAL","amount":"200.00"}' | jq

# get balance
curl -s http://localhost:8080/ledgers/$ID/balance | jq

# entry history (newest first)
curl -s "http://localhost:8080/ledgers/$ID?page=0&size=20" | jq

# list all ledgers
curl -s "http://localhost:8080/ledgers?page=0&size=20" | jq
```

Error cases:

```bash
# 422 (overdraft)
curl -s -X POST http://localhost:8080/ledgers/$ID \
  -H 'Content-Type: application/json' \
  -d '{"type":"WITHDRAWAL","amount":"99999.00"}' | jq

# 400 (zero amount)
curl -s -X POST http://localhost:8080/ledgers/$ID \
  -H 'Content-Type: application/json' \
  -d '{"type":"DEPOSIT","amount":"0"}' | jq

# 404 (ledger not found)
curl -s http://localhost:8080/ledgers/00000000-0000-0000-0000-000000000000/balance | jq
```

## Design notes

- **Running balance**: each `LedgerEntry` stores a `currentBalance` written at insert time. 
  - Balance reads are O(1) (`entries.getLast().currentBalance()`) rather than recomputing a sum across all entries on every request.
- **Entry ordering** — the entries list is reversed (not sorted) to get newest-first. 
  - Every write holds the per-ledger `ReentrantLock`, insertion order is guaranteed, making reversal O(n) vs O(n log n) for a `Comparator` sort.

## Datastore/Production considerations

- Would use Spring Data JPA + Hibernate on PostgreSQL. `LedgerRepository` is designed to extend `JpaRepository` directly. 
- The `ConcurrentHashMap` / `ReentrantLock` model maps to a pessimistic lock within a transaction. 
- Balance could be served from a Redis cache or a materialized view for high-volume ledgers.
