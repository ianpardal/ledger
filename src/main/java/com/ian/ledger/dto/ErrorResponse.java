package com.ian.ledger.dto;

import com.ian.ledger.exception.ErrorCode;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
    Instant timestamp, int status, ErrorCode error, String path, String message) {

  public static ErrorResponse of(ErrorCode error, String message, HttpStatus status, String path) {
    return new ErrorResponse(Instant.now(), status.value(), error, path, message);
  }
}
