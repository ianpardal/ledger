package com.ian.ledger.exception;

import com.ian.ledger.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(LedgerNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      LedgerNotFoundException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status)
        .body(
            ErrorResponse.of(
                ErrorCode.LEDGER_NOT_FOUND, ex.getMessage(), status, request.getRequestURI()));
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
}
