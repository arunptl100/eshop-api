package com.eshopapi.eshopapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int status;
  private String error;
  private String message;
  private String path;

  public ErrorResponse(int status, String error, String message, String path) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }
}
