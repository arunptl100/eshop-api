package com.eshopapi.eshopapi.exception;

public class RetryRequestFailedException extends RuntimeException {
  public RetryRequestFailedException(String message) {
    super(message);
  }
}
