package com.ian.ledger.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ian.ledger.dto.LedgerEntryResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.exception.InsufficientFundsException;
import com.ian.ledger.exception.LedgerNotFoundException;
import com.ian.ledger.model.EntryType;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.model.LedgerEntry;
import com.ian.ledger.repository.LedgerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerEntryServiceTest {

  LedgerRepository ledgerRepository;
  LedgerEntryService ledgerEntryService;

  @BeforeEach
  void setUp() {
    ledgerRepository = mock(LedgerRepository.class);
    ledgerEntryService = new LedgerEntryService(ledgerRepository);
  }

  @Test
  void addEntry_deposit_storesEntryWithCorrectBalance() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);

    // act
    LedgerEntry entry =
        ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("100.00"));

    // assert
    assertThat(entry.type()).isEqualTo(EntryType.DEPOSIT);
    assertThat(entry.amount()).isEqualByComparingTo("100.00");
    assertThat(entry.currentBalance()).isEqualByComparingTo("100.00");
    assertThat(ledger.entries()).hasSize(1);
  }

  @Test
  void addEntry_multipleDeposits_accumulatesBalance() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);

    // act
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("100.00"));
    LedgerEntry second =
        ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("50.00"));

    // assert
    assertThat(second.currentBalance()).isEqualByComparingTo("150.00");
  }

  @Test
  void addEntry_withdrawal_subtractsFromBalance() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("200.00"));

    // act
    LedgerEntry entry =
        ledgerEntryService.addEntry(ledgerId, EntryType.WITHDRAWAL, new BigDecimal("75.00"));

    // assert
    assertThat(entry.currentBalance()).isEqualByComparingTo("125.00");
  }

  @Test
  void addEntry_withdrawalExceedsBalance_throwsInsufficientFundsException() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("50.00"));

    // act & assert
    assertThatThrownBy(
            () ->
                ledgerEntryService.addEntry(
                    ledgerId, EntryType.WITHDRAWAL, new BigDecimal("100.00")))
        .isInstanceOf(InsufficientFundsException.class);
  }

  @Test
  void addEntry_unknownLedger_throwsLedgerNotFoundException() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    when(ledgerRepository.findByIdOrThrow(ledgerId))
        .thenThrow(new LedgerNotFoundException(ledgerId));

    // act / assert
    assertThatThrownBy(
            () ->
                ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("100.00")))
        .isInstanceOf(LedgerNotFoundException.class);
  }

  @Test
  void listEntries_returnsNewestFirst() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("10.00"));
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("20.00"));

    // act
    PaginationResponse<LedgerEntryResponse> result =
        ledgerEntryService.listEntries(ledgerId, 0, 20);

    // assert
    assertThat(result.data()).hasSize(2);
    assertThat(result.data().get(0).amount()).isEqualByComparingTo("20.00");
    assertThat(result.data().get(1).amount()).isEqualByComparingTo("10.00");
  }

  @Test
  void listEntries_paginatesCorrectly() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("10.00"));
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("20.00"));
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("30.00"));

    // act
    PaginationResponse<LedgerEntryResponse> result = ledgerEntryService.listEntries(ledgerId, 1, 2);

    // assert
    assertThat(result.data()).hasSize(1);
    assertThat(result.data().getFirst().amount()).isEqualByComparingTo("10.00");
    assertThat(result.total()).isEqualTo(3);
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void listEntries_emptyLedger_returnsEmptyPage() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);

    // act
    PaginationResponse<LedgerEntryResponse> result =
        ledgerEntryService.listEntries(ledgerId, 0, 20);

    // assert
    assertThat(result.data()).isEmpty();
    assertThat(result.total()).isZero();
  }

  @Test
  void getBalance_noEntries_returnsZero() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);

    // act
    BigDecimal balance = ledgerEntryService.getBalance(ledgerId);

    // assert
    assertThat(balance).isEqualByComparingTo("0.00");
  }

  @Test
  void getBalance_returnsRunningBalanceOfLatestEntry() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    Ledger ledger = new Ledger(ledgerId, "savings", Instant.now());
    when(ledgerRepository.findByIdOrThrow(ledgerId)).thenReturn(ledger);
    ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("100.00"));
    ledgerEntryService.addEntry(ledgerId, EntryType.WITHDRAWAL, new BigDecimal("30.00"));

    // act
    BigDecimal balance = ledgerEntryService.getBalance(ledgerId);

    // assert
    assertThat(balance).isEqualByComparingTo("70.00");
  }

  @Test
  void getBalance_unknownLedger_throwsLedgerNotFoundException() {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    when(ledgerRepository.findByIdOrThrow(ledgerId))
        .thenThrow(new LedgerNotFoundException(ledgerId));

    // act / assert
    assertThatThrownBy(() -> ledgerEntryService.getBalance(ledgerId))
        .isInstanceOf(LedgerNotFoundException.class);
  }
}
