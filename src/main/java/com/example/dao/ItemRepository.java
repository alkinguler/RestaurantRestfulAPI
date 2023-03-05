package com.example.dao;

import com.example.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "//TODO: ADD QUERY",nativeQuery = true)
    public Item getItemsOfMenuById(Long id);
}