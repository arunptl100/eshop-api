package com.eshopapi.eshopapi.controller;

import com.eshopapi.eshopapi.dto.CartDTO;
import com.eshopapi.eshopapi.dto.CheckoutResponseDTO;
import com.eshopapi.eshopapi.dto.ProductDTO;
import com.eshopapi.eshopapi.dto.ProductDataDTO;
import com.eshopapi.eshopapi.model.Cart;
import com.eshopapi.eshopapi.service.CartService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

  @Autowired private CartService cartService;

  @PostMapping
  public ResponseEntity<CartDTO> createCart(
      @Valid @Nullable @RequestBody List<ProductDataDTO> products) {
    if (products != null) {
      Cart cart = cartService.createCartWithProducts(products);
      return new ResponseEntity<>(convertToDTO(cart), HttpStatus.CREATED);
    } else {
      Cart cart = cartService.createCart();
      return new ResponseEntity<>(convertToDTO(cart), HttpStatus.CREATED);
    }
  }

  @GetMapping
  public ResponseEntity<List<CartDTO>> getAllCarts() {
    List<Cart> carts = cartService.getAllCarts();
    List<CartDTO> cartDtos = carts.stream().map(this::convertToDTO).collect(Collectors.toList());
    return ResponseEntity.ok(cartDtos);
  }

  private CartDTO convertToDTO(Cart cart) {
    List<ProductDTO> productDtos =
        cart.getCartItems().stream()
            .map(item -> new ProductDTO(item.getProduct().getProductId(), item.getQuantity()))
            .collect(Collectors.toList());
    return new CartDTO(cart.getCartId(), productDtos, cart.getCheckedOut());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CartDTO> modifyCart(
      @Valid @PathVariable("id") int cartId, @RequestBody List<ProductDataDTO> products) {
    Cart cart = cartService.modifyCart(cartId, products);
    return ResponseEntity.ok(convertToDTO(cart));
  }

  @PostMapping("/{id}/checkout")
  public ResponseEntity<CheckoutResponseDTO> checkoutCart(@PathVariable("id") int cartId) {
    Cart cart = cartService.checkoutCart(cartId);
    List<ProductDTO> productDtos =
        cart.getCartItems().stream()
            .map(item -> new ProductDTO(item.getProduct().getProductId(), item.getQuantity()))
            .collect(Collectors.toList());
    BigDecimal totalCost =
        cart.getCartItems().stream()
            .map(
                item ->
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    //      The reduce function adds sum of the multiplications, using 0 if none were performed
    CartDTO cartDto = new CartDTO(cart.getCartId(), productDtos, cart.getCheckedOut());
    CheckoutResponseDTO response = new CheckoutResponseDTO(cartDto, totalCost);
    return ResponseEntity.ok(response);
  }
}
