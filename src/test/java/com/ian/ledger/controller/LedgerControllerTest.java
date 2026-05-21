package com.ian.ledger.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LedgerControllerTest {

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new LedgerController()).build();
  }

  @Test
  void createLedger_returns201() throws Exception {
    // act
    var result = mockMvc.perform(post("/ledgers"));

    // assert
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.id").isString());
    result.andExpect(jsonPath("$.createdAt").isString());
  }

  @Test
  void listLedgers_returns200() throws Exception {
    // arrange
    int page = 0;
    int size = 20;

    // act
    var result =
        mockMvc.perform(
            get("/ledgers")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.data").isArray());
    result.andExpect(jsonPath("$.data[0].id").isString());
    result.andExpect(jsonPath("$.data[0].createdAt").isString());
    result.andExpect(jsonPath("$.page").value(page));
    result.andExpect(jsonPath("$.size").value(size));
    result.andExpect(jsonPath("$.total").isNumber());
  }

  @Test
  void getBalance_returns200() throws Exception {
    // arrange
    UUID ledgerId = UUID.randomUUID();

    // act
    var result = mockMvc.perform(get("/ledgers/{id}/balance", ledgerId));

    // assert
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.ledgerId").value(ledgerId.toString()));
    result.andExpect(jsonPath("$.balance").isNumber());
  }
}
