package com.eshopapi.eshopapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer cartId;

  @Column(name = "checked_out")
  private Boolean checkedOut = false;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<CartItem> cartItems = new ArrayList<>();

  public void addCartItem(CartItem item) {
    this.cartItems.add(item);
    item.setCart(this);
  }

  public void removeCartItem(CartItem item) {
    item.setCart(null);
    this.cartItems.remove(item);
  }
}
