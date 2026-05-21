package com.ian.ledger.controller;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for ledger entry management. */
@RestController
@RequestMapping("/ledgers/{id}/entries")
public class LedgerEntryController {

  /**
   * Records a deposit or withdrawal on a ledger.
   *
   * @param id the ledger ID
   * @param request the entry details
   * @return the created entry
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public String create(@PathVariable UUID id, @RequestBody Object request) {
    return "not implemented";
  }

  /**
   * Returns entry history for a ledger, newest first.
   *
   * @param id the ledger ID
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of entries
   */
  @GetMapping
  public String list(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return "not implemented";
  }
}
