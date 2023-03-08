package com.example.dao;

import com.example.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT I.*\n" +
            "FROM menu ME \n" +
            "INNER JOIN menu_item MI ON (ME.id = MI.menu_id)\n" +
            "INNER JOIN item I ON (MI.item_id = I.id)\n" +
            "INNER JOIN item_type IT ON (IT.id = I.item_type_id)\n" +
            "WHERE ME.id = ?", nativeQuery = true)
    public List<Item> findItemsOfAMenuById(Long id);

    @Query(value = "SELECT I.price\n" +
            "FROM menu ME \n" +
            "INNER JOIN menu_item MI ON (ME.id = MI.menu_id)\n" +
            "INNER JOIN item I ON (MI.item_id = I.id)\n" +
            "INNER JOIN item_type IT ON (IT.id = I.item_type_id)\n" +
            "WHERE I.id = ?", nativeQuery = true)
    public Integer findItemPriceById(Long id);

}