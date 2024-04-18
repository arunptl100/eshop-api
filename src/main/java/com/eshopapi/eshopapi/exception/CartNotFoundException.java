package com.eshopapi.eshopapi.exception;

public class CartNotFoundException extends RuntimeException {
  public CartNotFoundException(String message) {
    super(message);
  }
}
