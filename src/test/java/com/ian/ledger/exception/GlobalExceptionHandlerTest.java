package com.ian.ledger.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ian.ledger.dto.ErrorResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

  GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleNotFound_returns404() {
    // arrange
    UUID id = UUID.randomUUID();
    var ex = new LedgerNotFoundException(id);
    var request = new MockHttpServletRequest();
    request.setRequestURI("/ledgers/" + id);

    // act
    ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

    // assert
    assertErrorResponse(
        response,
        HttpStatus.NOT_FOUND,
        ErrorCode.LEDGER_NOT_FOUND,
        "No ledger with id " + id,
        "/ledgers/" + id);
  }

  @Test
  void handleTypeMismatch_returns400WithExpectedType() {
    // arrange
    var ex = new MethodArgumentTypeMismatchException("not-a-uuid", UUID.class, "id", null, null);
    var request = new MockHttpServletRequest();
    request.setRequestURI("/ledgers/not-a-uuid/balance");

    // act
    ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, request);

    // assert
    assertErrorResponse(
        response,
        HttpStatus.BAD_REQUEST,
        ErrorCode.INVALID_PARAMETER,
        "Invalid value for parameter 'id': 'not-a-uuid' is not a valid UUID",
        "/ledgers/not-a-uuid/balance");
  }

  @Test
  void handleValidation_returns400WithFirstFieldError() {
    // arrange
    var target = new com.ian.ledger.dto.CreateEntryRequest(null, null);
    var bindingResult = new BeanPropertyBindingResult(target, "createEntryRequest");
    bindingResult.rejectValue("amount", "Positive", "must be greater than 0");
    var ex = new MethodArgumentNotValidException(null, bindingResult);
    var request = new MockHttpServletRequest();
    request.setRequestURI("/ledgers/some-id");

    // act
    ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

    // assert
    assertErrorResponse(
        response,
        HttpStatus.BAD_REQUEST,
        ErrorCode.INVALID_PARAMETER,
        "amount: must be greater than 0",
        "/ledgers/some-id");
  }

  @Test
  void handleInsufficientFunds_returns422() {
    // arrange
    var ex = new InsufficientFundsException();
    var request = new MockHttpServletRequest();
    request.setRequestURI("/ledgers/some-id");

    // act
    ResponseEntity<ErrorResponse> response = handler.handleInsufficientFunds(ex, request);

    // assert
    assertErrorResponse(
        response,
        HttpStatus.UNPROCESSABLE_CONTENT,
        ErrorCode.INSUFFICIENT_FUNDS,
        "Insufficient funds for this withdrawal",
        "/ledgers/some-id");
  }

  @Test
  void handleUnexpected_returns500() {
    // arrange
    var ex = new RuntimeException("something went wrong");
    var request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/ledgers");

    // act
    ResponseEntity<ErrorResponse> response = handler.handleUnexpected(ex, request);

    // assert
    assertErrorResponse(
        response,
        HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCode.INTERNAL_ERROR,
        "An unexpected error occurred",
        "/ledgers");
  }

  private void assertErrorResponse(
      ResponseEntity<ErrorResponse> response,
      HttpStatus expectedStatus,
      ErrorCode expectedError,
      String expectedMessage,
      String expectedPath) {
    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().timestamp()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(expectedStatus.value());
    assertThat(response.getBody().error()).isEqualTo(expectedError);
    assertThat(response.getBody().message()).isEqualTo(expectedMessage);
    assertThat(response.getBody().path()).isEqualTo(expectedPath);
  }
}
