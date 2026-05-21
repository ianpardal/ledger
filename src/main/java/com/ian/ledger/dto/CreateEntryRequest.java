package com.ian.ledger.dto;

import com.ian.ledger.model.EntryType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateEntryRequest(
    @NotNull EntryType type,
    @NotNull @Positive @Digits(integer = 19, fraction = 2) BigDecimal amount) {}
