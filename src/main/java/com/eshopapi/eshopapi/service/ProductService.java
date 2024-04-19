package com.eshopapi.eshopapi.service;

import com.eshopapi.eshopapi.exception.InvalidProductLabelException;
import com.eshopapi.eshopapi.exception.ProductNameAlreadyExistsException;
import com.eshopapi.eshopapi.exception.ProductNotFoundException;
import com.eshopapi.eshopapi.exception.RetryRequestFailedException;
import com.eshopapi.eshopapi.model.CartItem;
import com.eshopapi.eshopapi.model.Product;
import com.eshopapi.eshopapi.repository.CartItemRepository;
import com.eshopapi.eshopapi.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
  public enum ProductLabel {
    DRINK,
    FOOD,
    CLOTHES,
    LIMITED;

    public static boolean isValidLabel(String label) {
      try {
        ProductLabel.valueOf(label.toUpperCase());
        return true;
      } catch (IllegalArgumentException e) {
        return false;
      }
    }
  }

  @Autowired private ProductRepository productRepository;

  @Autowired private CartItemRepository cartItemRepository;

  @Retryable(
      value = CannotAcquireLockException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // Delay of 1000ms between retries
      )
  @Transactional
  public Product saveProduct(Product product) {
    if (product.getLabels().stream().anyMatch(label -> !ProductLabel.isValidLabel(label))) {
      throw new InvalidProductLabelException("Label is not valid");
    }
    if (productRepository.existsByName(product.getName())) {
      throw new ProductNameAlreadyExistsException("A product with the same name already exists");
    }
    return productRepository.save(product);
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Transactional
  public Product getProductById(int productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> new ProductNotFoundException("Product with ID " + productId + " not found."));
  }

  @Retryable(
      value = CannotAcquireLockException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // Delay of 1000ms between retries
      )
  @Transactional
  public void deleteProduct(int productId) {
    if (!productRepository.existsById(productId)) {
      throw new ProductNotFoundException("Product with ID " + productId + " not found.");
    }
    //  delete the product from any cart its in
    List<CartItem> items = cartItemRepository.findByProductProductId(productId);
    items.forEach(cartItemRepository::delete);
    productRepository.deleteById(productId);
  }

  @Recover
  public void recover(CannotAcquireLockException e, int cartId, int productId, int quantity) {
    throw new RetryRequestFailedException(
        "The request on cart " + cartId + " failed after 3 retries");
  }
}
