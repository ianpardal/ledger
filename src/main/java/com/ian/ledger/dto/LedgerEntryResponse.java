package com.ian.ledger.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ian.ledger.model.EntryType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
    UUID id,
    EntryType type,
    @JsonSerialize(using = ToStringSerializer.class) BigDecimal amount,
    Instant createdAt) {}
