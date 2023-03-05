package com.example.dao;

import com.example.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query(value = "SELECT * FROM menu WHERE LOWER(day) LIKE lower( CONCAT('%', ?, '%'))", nativeQuery = true)
    public Menu findByDay(String day);



}