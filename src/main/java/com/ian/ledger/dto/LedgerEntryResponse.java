package com.ian.ledger.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ian.ledger.model.EntryType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// TODO: mirrors LedgerEntry fields today, but kept as a separate DTO to enforce the
// controller/domain boundary — in a real project this would likely diverge (e.g. computed fields,
// different serialization, hiding internal state)
public record LedgerEntryResponse(
    UUID id,
    EntryType type,
    @JsonSerialize(using = ToStringSerializer.class) BigDecimal amount,
    @JsonSerialize(using = ToStringSerializer.class) BigDecimal currentBalance,
    Instant createdAt) {}
