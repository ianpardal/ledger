package com.ian.ledger.service;

import com.ian.ledger.dto.LedgerResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.repository.LedgerRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LedgerService {

  private final LedgerRepository ledgerRepository;

  public LedgerService(LedgerRepository ledgerRepository) {
    this.ledgerRepository = ledgerRepository;
  }

  /**
   * Creates a new ledger with a generated UUID.
   *
   * @return the created ledger
   */
  public Ledger createLedger(String name) {
    return ledgerRepository.save(new Ledger(UUID.randomUUID(), name, Instant.now()));
  }

  /**
   * Returns all ledgers, newest first. Offset pagination is used here via page/size.
   *
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of ledgers
   */
  public PaginationResponse<LedgerResponse> listLedgers(int page, int size) {
    // getting the ledgers sorted by newest
    // ideally wouldn't load all entries into memory, would use a DB instead!
    List<Ledger> newestSortedLedgers =
        ledgerRepository.findAll().stream()
            .sorted(Comparator.comparing(Ledger::createdAt).reversed())
            .toList();

    // getting the paginated ledgers based upon the above.
    List<LedgerResponse> pagedLedgers =
        newestSortedLedgers.stream()
            .skip((long) page * size)
            .limit(size)
            .map(l -> new LedgerResponse(l.id(), l.name(), l.createdAt()))
            .toList();

    return new PaginationResponse<>(pagedLedgers, page, size, newestSortedLedgers.size());
  }
}
