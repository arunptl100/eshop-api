package com.eshopapi.eshopapi.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class CartDTO {
  private Integer cartId;
  @Nullable private List<ProductDTO> products;
  private Boolean checkedOut;

  public CartDTO(Integer cartId, List<ProductDTO> products, Boolean checkedOut) {
    this.cartId = cartId;
    this.products = products;
    this.checkedOut = checkedOut;
  }
}
