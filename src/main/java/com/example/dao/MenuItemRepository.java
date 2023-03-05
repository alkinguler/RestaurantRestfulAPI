package com.example.dao;

import com.example.model.MenuItem;
import com.example.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
