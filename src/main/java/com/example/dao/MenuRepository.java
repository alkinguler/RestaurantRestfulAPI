package com.example.dao;

import com.example.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query(value = "SELECT * FROM menu WHERE LOWER(day) LIKE lower( CONCAT('%', ?, '%'))", nativeQuery = true)
    public Menu findByDay(String day);

    @Query(value = "SELECT id FROM menu WHERE LOWER(day) LIKE lower( CONCAT('%', ?, '%'))", nativeQuery = true)
    public List<Long> findMenuIdByDay(String day);

}