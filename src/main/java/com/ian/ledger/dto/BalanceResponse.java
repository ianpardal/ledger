package com.ian.ledger.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(
    UUID ledgerId, @JsonSerialize(using = ToStringSerializer.class) BigDecimal balance) {}
