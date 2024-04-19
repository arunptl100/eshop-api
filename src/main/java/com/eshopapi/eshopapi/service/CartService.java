package com.eshopapi.eshopapi.service;

import com.eshopapi.eshopapi.dto.ProductDataDTO;
import com.eshopapi.eshopapi.exception.CartAlreadyCheckedOutException;
import com.eshopapi.eshopapi.exception.CartNotFoundException;
import com.eshopapi.eshopapi.exception.ProductNotFoundException;
import com.eshopapi.eshopapi.exception.RetryRequestFailedException;
import com.eshopapi.eshopapi.model.Cart;
import com.eshopapi.eshopapi.model.CartItem;
import com.eshopapi.eshopapi.model.Product;
import com.eshopapi.eshopapi.repository.CartItemRepository;
import com.eshopapi.eshopapi.repository.CartRepository;
import com.eshopapi.eshopapi.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  @Autowired private CartRepository cartRepository;

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private ProductRepository productRepository;

  @Transactional
  @Retryable(
      value = CannotAcquireLockException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // Delay of 1000ms between retries
      )
  public Cart createCartWithProducts(List<ProductDataDTO> productDataList) {
    Cart cart = new Cart();
    cart.setCartItems(
        productDataList.stream()
            .map(
                pd -> {
                  Product product =
                      productRepository
                          .findById(pd.getProductId())
                          .orElseThrow(
                              () ->
                                  new ProductNotFoundException(
                                      "Product not found: " + pd.getProductId()));
                  CartItem item = new CartItem();
                  item.setProduct(product);
                  item.setQuantity(pd.getQuantity());
                  item.setCart(cart);
                  return item;
                })
            .collect(Collectors.toList()));
    return cartRepository.save(cart);
  }

  //  Creates an empty cart with no products
  @Transactional
  public Cart createCart() {
    Cart cart = new Cart();
    return cartRepository.save(cart);
  }

  public List<Cart> getAllCarts() {
    return cartRepository.findAll();
  }

  @Retryable(
      value = CannotAcquireLockException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // Delay of 1000ms between retries
      )
  @Transactional
  public Cart modifyCart(int cartId, List<ProductDataDTO> items) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
    if (cart.getCheckedOut()) {
      throw new CartAlreadyCheckedOutException("Cannot modify a checked out cart");
    }

    for (ProductDataDTO newItem : items) {
      Product product =
          productRepository
              .findById(newItem.getProductId())
              .orElseThrow(
                  () ->
                      new ProductNotFoundException(
                          "Product not found with ID: " + newItem.getProductId()));

      Optional<CartItem> existingItem =
          cart.getCartItems().stream()
              .filter(item -> item.getProduct().getProductId().equals(newItem.getProductId()))
              .findFirst();

      if (existingItem.isPresent()) {
        // If the item exists, just add to its quantity
        CartItem item = existingItem.get();
        item.setQuantity(item.getQuantity() + newItem.getQuantity());
        cartItemRepository.save(item);
      } else {
        // If the item does not exist, add it to the cart
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(newItem.getQuantity());
        cartItem.setCart(cart);
        cart.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);
      }
    }

    return cartRepository.save(cart);
  }

  @Retryable(
      value = CannotAcquireLockException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // Delay of 1000ms between retries
      )
  @Transactional
  public Cart checkoutCart(int cartId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart " + cartId + " not found"));
    if (cart.getCheckedOut()) {
      throw new CartAlreadyCheckedOutException("Cart " + cartId + " is already checked out.");
    }
    cart.setCheckedOut(true);
    return cartRepository.save(cart);
  }

  @Recover
  public void recover(CannotAcquireLockException e, int cartId, int productId, int quantity) {
    throw new RetryRequestFailedException(
        "The request on cart " + cartId + " failed after 3 retries");
  }
}
