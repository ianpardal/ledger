package com.ian.ledger.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LedgerEntryControllerTest {

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new LedgerEntryController()).build();
  }

  @Test
  void createEntry_returns201() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();
    String body =
        """
        {"type": "DEPOSIT", "amount": "100.00"}
        """;

    // act
    var result =
        mockMvc.perform(
            post("/ledgers/{id}/entries", ledgerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

    // assert
    result.andExpect(status().isCreated());
    result.andExpect(
        content()
            .string("not implemented")); // TODO: add actual expected return when we have models
  }

  @Test
  void listEntries_returns200() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();

    // act
    var result = mockMvc.perform(get("/ledgers/{id}/entries", ledgerId));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(
        content()
            .string("not implemented")); // TODO: add actual expected return when we have models
  }
}
