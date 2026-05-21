package com.ian.ledger.controller;

import com.ian.ledger.dto.LedgerEntryResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.EntryType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
  public LedgerEntryResponse create(@PathVariable UUID id, @RequestBody Object request) {
    // TODO: add actual implementation with LedgerService
    return new LedgerEntryResponse(
        UUID.randomUUID(), EntryType.DEPOSIT, BigDecimal.ZERO, Instant.now());
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
  public PaginationResponse<LedgerEntryResponse> list(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    ArrayList<LedgerEntryResponse> entries = new ArrayList<>();
    entries.add(
        new LedgerEntryResponse(
            UUID.randomUUID(), EntryType.DEPOSIT, BigDecimal.ZERO, Instant.now()));

    // TODO: add actual implementation with LedgerService
    return new PaginationResponse<>(entries, page, size, 1);
  }
}
