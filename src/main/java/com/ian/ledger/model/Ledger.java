package com.ian.ledger.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public record Ledger(UUID id, Instant createdAt, List<LedgerEntry> entries, ReentrantLock lock) {

  public Ledger(UUID id, Instant createdAt) {
    this(id, createdAt, new ArrayList<>(), new ReentrantLock());
  }
}
