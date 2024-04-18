package com.eshopapi.eshopapi.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutResponseDTO {
  private CartDTO cart;
  private BigDecimal totalCost;

  public CheckoutResponseDTO(CartDTO cart, BigDecimal totalCost) {
    this.cart = cart;
    this.totalCost = totalCost;
  }
}
