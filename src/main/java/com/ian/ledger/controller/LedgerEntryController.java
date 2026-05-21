package com.ian.ledger.controller;

import com.ian.ledger.dto.BalanceResponse;
import com.ian.ledger.dto.CreateEntryRequest;
import com.ian.ledger.dto.LedgerEntryResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.LedgerEntry;
import com.ian.ledger.service.LedgerEntryService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/ledgers/{id}")
public class LedgerEntryController {

  private final LedgerEntryService ledgerEntryService;

  public LedgerEntryController(LedgerEntryService ledgerEntryService) {
    this.ledgerEntryService = ledgerEntryService;
  }

  /**
   * Records a deposit or withdrawal on a ledger.
   *
   * @param id the ledger ID
   * @param request the entry details
   * @return the created entry
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public LedgerEntryResponse create(
      @PathVariable UUID id, @Valid @RequestBody CreateEntryRequest request) {
    LedgerEntry entry = ledgerEntryService.addEntry(id, request.type(), request.amount());
    return new LedgerEntryResponse(
        entry.id(), entry.type(), entry.amount(), entry.currentBalance(), entry.createdAt());
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
    return ledgerEntryService.listEntries(id, page, size);
  }

  /**
   * Returns the current balance for the given ledger id.
   *
   * @param id the ledger ID
   * @return the id + balance
   */
  @GetMapping("balance")
  public BalanceResponse getBalance(@PathVariable UUID id) {
    return new BalanceResponse(id, ledgerEntryService.getBalance(id));
  }
}
