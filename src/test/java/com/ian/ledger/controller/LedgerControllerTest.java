package com.ian.ledger.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ian.ledger.dto.LedgerResponse;
import com.ian.ledger.dto.PaginationResponse;
import com.ian.ledger.model.Ledger;
import com.ian.ledger.service.LedgerService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LedgerControllerTest {

  LedgerService ledgerService;
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    ledgerService = mock(LedgerService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new LedgerController(ledgerService)).build();
  }

  @Test
  void createLedger_returns201() throws Exception {
    // arrange
    Ledger ledger = new Ledger(UUID.randomUUID(), "savings", Instant.now());
    when(ledgerService.createLedger("savings")).thenReturn(ledger);

    // act
    var result =
        mockMvc.perform(
            post("/ledgers")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"name\":\"savings\"}"));

    // assert
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.id").value(ledger.id().toString()));
    result.andExpect(jsonPath("$.name").value("savings"));
    result.andExpect(jsonPath("$.createdAt").isString());
  }

  @Test
  void listLedgers_returns200() throws Exception {
    // arrange
    int page = 0;
    int size = 20;
    Ledger ledger = new Ledger(UUID.randomUUID(), "savings", Instant.now());
    LedgerResponse ledgerResponse =
        new LedgerResponse(ledger.id(), ledger.name(), ledger.createdAt());
    when(ledgerService.listLedgers(page, size))
        .thenReturn(new PaginationResponse<>(List.of(ledgerResponse), page, size, 1));

    // act
    var result =
        mockMvc.perform(
            get("/ledgers")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.data[0].id").value(ledger.id().toString()));
    result.andExpect(jsonPath("$.data[0].createdAt").isString());
    result.andExpect(jsonPath("$.page").value(page));
    result.andExpect(jsonPath("$.size").value(size));
    result.andExpect(jsonPath("$.total").value(1));
  }
}
