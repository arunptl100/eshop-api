package com.eshopapi.eshopapi.repository;

import com.eshopapi.eshopapi.model.CartItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
  List<CartItem> findByProductProductId(Integer productId);
}
