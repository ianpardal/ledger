package com.ian.ledger.repository;

import com.ian.ledger.exception.LedgerNotFoundException;
import com.ian.ledger.model.Ledger;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepository {

  Ledger save(Ledger ledger);

  Optional<Ledger> findById(UUID id);

  Collection<Ledger> findAll();

  default Ledger findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(() -> new LedgerNotFoundException(id));
  }
}
