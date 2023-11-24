package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT s FROM tblShoppingCart s WHERE s.ID = :shoppingCartId")
    ShoppingCart getShoppingCartByID(long shoppingCartId);
}
