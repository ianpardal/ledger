package com.ian.ledger.controller;

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
  public String create() {
    return "not implemented";
  }

  /**
   * Returns all ledgers, newest first.
   *
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of ledgers
   */
  @GetMapping
  public String list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return "not implemented";
  }

  /**
   * Returns the current balance for a ledger.
   *
   * @param id the ledger ID
   * @return the balance
   */
  @GetMapping("/{id}/balance")
  public String balance(@PathVariable UUID id) {
    return "not implemented";
  }
}
