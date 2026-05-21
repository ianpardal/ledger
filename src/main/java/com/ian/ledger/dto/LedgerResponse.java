package com.ian.ledger.dto;

import java.time.Instant;
import java.util.UUID;

public record LedgerResponse(UUID id, String name, Instant createdAt) {}
