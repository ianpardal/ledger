package com.ian.ledger.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    result.andExpect(jsonPath("$.id").isString());
    result.andExpect(jsonPath("$.type").value("DEPOSIT"));
    result.andExpect(jsonPath("$.amount").isNumber());
    result.andExpect(jsonPath("$.createdAt").isString());
  }

  @Test
  void listEntries_returns200() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();

    // act
    var result = mockMvc.perform(get("/ledgers/{id}/entries", ledgerId));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.data").isArray());
    result.andExpect(jsonPath("$.data[0].id").isString());
    result.andExpect(jsonPath("$.data[0].type").isString());
    result.andExpect(jsonPath("$.data[0].amount").isNumber());
    result.andExpect(jsonPath("$.data[0].createdAt").isString());
    result.andExpect(jsonPath("$.page").value(0));
    result.andExpect(jsonPath("$.size").value(20));
    result.andExpect(jsonPath("$.total").isNumber());
  }
}
