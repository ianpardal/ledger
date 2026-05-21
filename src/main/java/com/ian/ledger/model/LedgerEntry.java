package com.ian.ledger.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntry(UUID id, EntryType type, BigDecimal amount, Instant createdAt) {}
