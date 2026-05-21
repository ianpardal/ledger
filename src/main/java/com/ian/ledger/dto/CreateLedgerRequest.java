package com.ian.ledger.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLedgerRequest(@NotBlank String name) {}
