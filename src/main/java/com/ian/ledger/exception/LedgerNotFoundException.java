package com.ian.ledger.exception;

import java.util.UUID;

public class LedgerNotFoundException extends RuntimeException {

  public LedgerNotFoundException(UUID id) {
    super("No ledger with id " + id);
  }
}
