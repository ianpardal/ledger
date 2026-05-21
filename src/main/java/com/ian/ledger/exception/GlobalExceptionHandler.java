package com.ian.ledger.exception;

import com.ian.ledger.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(LedgerNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      LedgerNotFoundException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status)
        .body(
            ErrorResponse.of(
                ErrorCode.LEDGER_NOT_FOUND, ex.getMessage(), status, request.getRequestURI()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .sorted()
            .findFirst()
            .orElse("Invalid request");
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.of(
                ErrorCode.INVALID_PARAMETER,
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String expectedType =
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
    String message =
        "Invalid value for parameter '"
            + ex.getName()
            + "': '"
            + ex.getValue()
            + "' is not a valid "
            + expectedType;
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.of(
                ErrorCode.INVALID_PARAMETER,
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()));
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientFunds(
      InsufficientFundsException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;

    return ResponseEntity.status(status)
        .body(
            ErrorResponse.of(
                ErrorCode.INSUFFICIENT_FUNDS, ex.getMessage(), status, request.getRequestURI()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
    // TODO: ideally add observability integration for team alerts here; new relic or Sentry or
    // TODO: equivalent
    log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status)
        .body(
            ErrorResponse.of(
                ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred",
                status,
                request.getRequestURI()));
  }
}
