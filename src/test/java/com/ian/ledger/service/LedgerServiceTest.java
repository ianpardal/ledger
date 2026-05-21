package com.ian.ledger.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ian.ledger.dto.LedgerResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.repository.LedgerRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerServiceTest {

  LedgerRepository ledgerRepository;
  LedgerService ledgerService;

  @BeforeEach
  void setUp() {
    ledgerRepository = mock(LedgerRepository.class);
    ledgerService = new LedgerService(ledgerRepository);
  }

  @Test
  void createLedger_savesAndReturnsLedger() {
    // arrange
    Ledger saved = new Ledger(UUID.randomUUID(), "savings", Instant.now());
    when(ledgerRepository.save(any(Ledger.class))).thenReturn(saved);

    // act
    Ledger result = ledgerService.createLedger("savings");

    // assert
    assertThat(result.id()).isEqualTo(saved.id());
    assertThat(result.name()).isEqualTo("savings");
    assertThat(result.createdAt()).isEqualTo(saved.createdAt());
  }

  @Test
  void listLedgers_returnsPagedAndMappedResponse() {
    // arrange
    Ledger ledger = new Ledger(UUID.randomUUID(), "savings", Instant.now());
    when(ledgerRepository.findAll()).thenReturn(List.of(ledger));

    // act
    PaginationResponse<LedgerResponse> result = ledgerService.listLedgers(0, 20);

    // assert
    assertThat(result.data()).hasSize(1);
    assertThat(result.data().get(0).id()).isEqualTo(ledger.id());
    assertThat(result.data().get(0).createdAt()).isEqualTo(ledger.createdAt());
    assertThat(result.page()).isZero();
    assertThat(result.size()).isEqualTo(20);
    assertThat(result.total()).isEqualTo(1);
  }

  @Test
  void listLedgers_sortsNewestFirst() {
    // arrange
    Ledger older = new Ledger(UUID.randomUUID(), "savings", Instant.parse("2024-01-01T00:00:00Z"));
    Ledger newer = new Ledger(UUID.randomUUID(), "savings", Instant.parse("2024-06-01T00:00:00Z"));
    when(ledgerRepository.findAll()).thenReturn(List.of(older, newer));

    // act
    PaginationResponse<LedgerResponse> result = ledgerService.listLedgers(0, 20);

    // assert
    assertThat(result.data().get(0).id()).isEqualTo(newer.id());
    assertThat(result.data().get(1).id()).isEqualTo(older.id());
  }

  @Test
  void listLedgers_slicesCorrectlyForSecondPage() {
    // arrange
    List<Ledger> ledgers =
        List.of(
            new Ledger(UUID.randomUUID(), "savings", Instant.parse("2024-03-01T00:00:00Z")),
            new Ledger(UUID.randomUUID(), "savings", Instant.parse("2024-02-01T00:00:00Z")),
            new Ledger(UUID.randomUUID(), "savings", Instant.parse("2024-01-01T00:00:00Z")));
    when(ledgerRepository.findAll()).thenReturn(ledgers);

    // act
    PaginationResponse<LedgerResponse> result = ledgerService.listLedgers(1, 2);

    // assert
    assertThat(result.data()).hasSize(1);
    assertThat(result.total()).isEqualTo(3);
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void listLedgers_emptyRepository_returnsEmptyPage() {
    // arrange
    when(ledgerRepository.findAll()).thenReturn(List.of());

    // act
    PaginationResponse<LedgerResponse> result = ledgerService.listLedgers(0, 20);

    // assert
    assertThat(result.data()).isEmpty();
    assertThat(result.total()).isZero();
  }
}
