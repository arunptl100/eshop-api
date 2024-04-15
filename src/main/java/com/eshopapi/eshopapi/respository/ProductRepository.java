package com.eshopapi.eshopapi.respository;

import com.eshopapi.eshopapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Custom database queries can be added here if necessary
}
