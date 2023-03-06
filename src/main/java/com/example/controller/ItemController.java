package com.example.controller;

import com.example.model.Item;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
    @RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {
    private final MenuService menuService;

    @GetMapping("/getItemsOfMenuById/{id}")
    public List<Item> getItemsOfMenuById(@PathVariable Long id){
        return menuService.getItemsOfAMenuById(id);
    }

    @GetMapping("/findMenuIdByDay/{day}")
    public List<Item> getItemsOfAMenuByDay(@PathVariable String day){
        return menuService.getItemsOfAMenuByDay(day);
    }
}
