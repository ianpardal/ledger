package com.ian.ledger.controller;

import com.ian.ledger.dto.BalanceResponse;
import com.ian.ledger.dto.LedgerResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.Ledger;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for ledger management. */
@RestController
@RequestMapping("/ledgers")
public class LedgerController {

  /**
   * Creates a new ledger.
   *
   * @return the created ledger
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LedgerResponse create() {
    // TODO: add actual implementation with LedgerService
    return new LedgerResponse(UUID.randomUUID(), Instant.now());
  }

  /**
   * Returns all ledgers, newest first.
   *
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of ledgers
   */
  @GetMapping
  public PaginationResponse<Ledger> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    UUID ledgerId = UUID.randomUUID();
    ArrayList<Ledger> ledgers = new ArrayList<>();
    ledgers.add(new Ledger(ledgerId, Instant.now()));

    // TODO: add actual implementation with LedgerService
    return new PaginationResponse<>(ledgers, page, size, 1);
  }

  /**
   * Returns the current balance for a ledger.
   *
   * @param id the ledger ID
   * @return the balance
   */
  @GetMapping("/{id}/balance")
  public BalanceResponse balance(@PathVariable UUID id) {

    // TODO: add actual implementation with LedgerService
    return new BalanceResponse(id, BigDecimal.ZERO);
  }
}
