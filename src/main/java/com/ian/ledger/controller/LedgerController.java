package com.ian.ledger.controller;

import com.ian.ledger.dto.CreateLedgerRequest;
import com.ian.ledger.dto.LedgerResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.service.LedgerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for ledger management. */
@RestController
@RequestMapping("/ledgers")
public class LedgerController {

  private final LedgerService ledgerService;

  public LedgerController(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

  /**
   * Creates a new ledger.
   *
   * @return the created ledger
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LedgerResponse create(@Valid @RequestBody CreateLedgerRequest request) {
    Ledger ledger = ledgerService.createLedger(request.name());
    return new LedgerResponse(ledger.id(), ledger.name(), ledger.createdAt());
  }

  /**
   * Returns all ledgers, newest first.
   *
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of ledgers
   */
  @GetMapping
  public PaginationResponse<LedgerResponse> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ledgerService.listLedgers(page, size);
  }
}
