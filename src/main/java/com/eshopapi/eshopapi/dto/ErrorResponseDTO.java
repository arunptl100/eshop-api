package com.eshopapi.eshopapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {
  private int status;
  private String error;
  private String message;
  private String path;

  public ErrorResponseDTO(int status, String error, String message, String path) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }
}
