package com.ian.ledger.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ian.ledger.dto.LedgerEntryResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.EntryType;
import com.ian.ledger.model.LedgerEntry;
import com.ian.ledger.service.LedgerEntryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LedgerEntryControllerTest {

  LedgerEntryService ledgerEntryService;
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    ledgerEntryService = mock(LedgerEntryService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new LedgerEntryController(ledgerEntryService)).build();
  }

  @Test
  void createEntry_returns201() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    LedgerEntry entry =
        new LedgerEntry(
            UUID.randomUUID(),
            EntryType.DEPOSIT,
            new BigDecimal("100.00"),
            new BigDecimal("100.00"),
            Instant.now());
    when(ledgerEntryService.addEntry(ledgerId, EntryType.DEPOSIT, new BigDecimal("100.00")))
        .thenReturn(entry);

    // act
    var result =
        mockMvc.perform(
            post("/ledgers/{id}", ledgerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"DEPOSIT\",\"amount\":\"100.00\"}"));

    // assert
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.id").value(entry.id().toString()));
    result.andExpect(jsonPath("$.type").value("DEPOSIT"));
    result.andExpect(jsonPath("$.amount").isNumber());
    result.andExpect(jsonPath("$.currentBalance").isNumber());
    result.andExpect(jsonPath("$.createdAt").isString());
  }

  @Test
  void listEntries_returns200() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    LedgerEntry entry =
        new LedgerEntry(
            UUID.randomUUID(),
            EntryType.DEPOSIT,
            new BigDecimal("50.00"),
            new BigDecimal("50.00"),
            Instant.now());
    LedgerEntryResponse entryResponse =
        new LedgerEntryResponse(
            entry.id(), entry.type(), entry.amount(), entry.currentBalance(), entry.createdAt());
    when(ledgerEntryService.listEntries(ledgerId, 0, 20))
        .thenReturn(new PaginationResponse<>(List.of(entryResponse), 0, 20, 1));

    // act
    var result = mockMvc.perform(get("/ledgers/{id}", ledgerId));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.data[0].id").value(entry.id().toString()));
    result.andExpect(jsonPath("$.data[0].type").value("DEPOSIT"));
    result.andExpect(jsonPath("$.data[0].amount").isNumber());
    result.andExpect(jsonPath("$.data[0].createdAt").isString());
    result.andExpect(jsonPath("$.page").value(0));
    result.andExpect(jsonPath("$.size").value(20));
    result.andExpect(jsonPath("$.total").value(1));
  }

  @Test
  void getBalance_returns200() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    when(ledgerEntryService.getBalance(ledgerId)).thenReturn(new BigDecimal("150.00"));

    // act
    var result = mockMvc.perform(get("/ledgers/{id}/balance", ledgerId));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.ledgerId").value(ledgerId.toString()));
    result.andExpect(jsonPath("$.balance").isNumber());
  }
}
