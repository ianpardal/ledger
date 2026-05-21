package com.ian.ledger.service;

import com.ian.ledger.dto.LedgerEntryResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.exception.InsufficientFundsException;
import com.ian.ledger.model.EntryType;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.model.LedgerEntry;
import com.ian.ledger.repository.LedgerRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LedgerEntryService {

  private final LedgerRepository ledgerRepository;

  public LedgerEntryService(LedgerRepository ledgerRepository) {
    this.ledgerRepository = ledgerRepository;
  }

  /**
   * Records a deposit or withdrawal on a ledger.
   *
   * @param id the ledger ID
   * @param type whether this is a deposit or withdrawal
   * @param amount the amount to apply, must be positive
   * @return the created entry with its running balance
   * @throws com.ian.ledger.exception.LedgerNotFoundException if no ledger exists with the given ID
   * @throws com.ian.ledger.exception.InsufficientFundsException if a withdrawal would make the
   *     balance negative
   */
  public LedgerEntry addEntry(UUID id, EntryType type, BigDecimal amount) {
    // retrieve ledger + lock it to prevent any operations on it
    Ledger ledger = ledgerRepository.findByIdOrThrow(id);
    ledger.mutex().lock();

    try {
      // calculating the new balance given the amount being subtracted
      BigDecimal newBalance = calculateNewBalance(type, amount, ledger);

      // adding a new entry with the new balance, returning
      LedgerEntry entry =
          new LedgerEntry(UUID.randomUUID(), type, amount, newBalance, Instant.now());
      ledger.entries().add(entry);
      return entry;
    } finally {
      // releasing the lock to avoid deadlocks
      ledger.mutex().unlock();
    }
  }

  /**
   * Returns entry history for a ledger, newest first.
   *
   * @param id the ledger ID
   * @param page zero-based page index
   * @param size number of items per page
   * @return paginated list of entries
   * @throws com.ian.ledger.exception.LedgerNotFoundException if no ledger exists with the given ID
   */
  public PaginationResponse<LedgerEntryResponse> listEntries(UUID id, int page, int size) {
    // we get the ledger and lock it to avoid ConcurrentModificationExceptions
    Ledger ledger = ledgerRepository.findByIdOrThrow(id);
    ledger.mutex().lock();

    try {
      // reversing the copy to get newest-first; comparing createdAt would also work but costs
      // O(n log n)!
      // so in our case, insertion order is reliable here because addEntry holds the lock
      List<LedgerEntry> newestSortedEntries = new ArrayList<>(ledger.entries());
      Collections.reverse(newestSortedEntries);

      // getting the page requested
      List<LedgerEntryResponse> pagedEntries =
          newestSortedEntries.stream()
              .skip((long) page * size)
              .limit(size)
              .map(
                  e ->
                      new LedgerEntryResponse(
                          e.id(), e.type(), e.amount(), e.currentBalance(), e.createdAt()))
              .toList();

      return new PaginationResponse<>(pagedEntries, page, size, newestSortedEntries.size());
    } finally {
      // releasing the lock to avoid deadlocks
      ledger.mutex().unlock();
    }
  }

  /**
   * Returns the current balance for a ledger by reading the most recent entry's running balance.
   *
   * @param id the ledger ID
   * @return the current balance, or zero if the ledger has no entries
   * @throws com.ian.ledger.exception.LedgerNotFoundException if no ledger exists with the given ID
   */
  public BigDecimal getBalance(UUID id) {
    // we get the ledger and lock it to avoid ConcurrentModificationExceptions
    Ledger ledger = ledgerRepository.findByIdOrThrow(id);
    ledger.mutex().lock();

    try {
      // entries are in insertion order (guaranteed by the lock), so the last element is newest
      List<LedgerEntry> entries = ledger.entries();

      return entries.isEmpty()
          ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
          : entries.getLast().currentBalance();
    } finally {
      // releasing the lock to avoid deadlocks
      ledger.mutex().unlock();
    }
  }

  private BigDecimal calculateNewBalance(EntryType type, BigDecimal amount, Ledger ledger) {
    // getting the latest balance by obtaining the newest entry into List
    List<LedgerEntry> entries = ledger.entries();
    BigDecimal latestBalance =
        entries.isEmpty()
            ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : entries.getLast().currentBalance();

    // if we're withdrawing, we can't go negative here
    if (type == EntryType.WITHDRAWAL && latestBalance.compareTo(amount) < 0) {
      throw new InsufficientFundsException();
    }

    // if we're depositing, we add, otherwise, subtract!
    return type == EntryType.DEPOSIT
        ? latestBalance.add(amount).setScale(2, RoundingMode.HALF_UP)
        : latestBalance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
  }
}
