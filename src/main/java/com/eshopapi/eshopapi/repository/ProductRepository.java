package com.eshopapi.eshopapi.repository;

import com.eshopapi.eshopapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
  boolean existsByName(String name);
}
