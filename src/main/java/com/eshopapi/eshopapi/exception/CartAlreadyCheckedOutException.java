package com.eshopapi.eshopapi.exception;

public class CartAlreadyCheckedOutException extends RuntimeException {
  public CartAlreadyCheckedOutException(String message) {
    super(message);
  }
}
