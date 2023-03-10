package com.example.dao;

import com.example.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = "SELECT oi.* FROM order_item oi \n" +
            "INNER JOIN item i ON(oi.item_id = i.id)\n" +
            "WHERE oi.order_id = ?",nativeQuery = true)
    public List<OrderItem> findItemAndQuan(Long id);
}