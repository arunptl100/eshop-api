package com.eshopapi.eshopapi.service;

import com.eshopapi.eshopapi.exception.InvalidProductLabelException;
import com.eshopapi.eshopapi.exception.ProductNameAlreadyExistsException;
import com.eshopapi.eshopapi.model.Product;
import com.eshopapi.eshopapi.respository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    public enum ProductLabel {
        DRINK, FOOD, CLOTHES, LIMITED;

        public static boolean isValidLabel(String label) {
            try {
                ProductLabel.valueOf(label.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    @Autowired
    private ProductRepository productRepository;

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

    public Product getProductById(int productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(int productId) {
        productRepository.deleteById(productId);
    }
}
