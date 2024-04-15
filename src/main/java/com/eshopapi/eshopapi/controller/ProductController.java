package com.eshopapi.eshopapi.controller;

import com.eshopapi.eshopapi.model.Product;
import com.eshopapi.eshopapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") int productId) {
        return productService.getProductById(productId);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") int productId) {
        productService.deleteProduct(productId);
    }
}
