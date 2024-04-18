package com.eshopapi.eshopapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
  private Integer productId;
  private Integer quantity;

  public ProductDTO(Integer productId, Integer quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }
}
