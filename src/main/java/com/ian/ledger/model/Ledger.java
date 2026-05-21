package com.ian.ledger.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public record Ledger(
    UUID id, String name, Instant createdAt, List<LedgerEntry> entries, ReentrantLock mutex) {

  public Ledger(UUID id, String name, Instant createdAt) {
    this(id, name, createdAt, new ArrayList<>(), new ReentrantLock());
  }
}
