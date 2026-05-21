package com.ian.ledger.repository;

import com.ian.ledger.model.Ledger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLedgerRepository implements LedgerRepository {

  private final Map<UUID, Ledger> store = new ConcurrentHashMap<>();

  @Override
  public Ledger save(Ledger ledger) {
    store.put(ledger.id(), ledger);
    return ledger;
  }

  @Override
  public Optional<Ledger> findById(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public Collection<Ledger> findAll() {
    return store.values();
  }
}
