package com.example.controller;

import com.example.model.Item;
import com.example.model.Menu;
import com.example.service.MenuService;
import com.example.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    //private final RestaurantService restaurantService;

    @GetMapping()
    public List<Menu> findAllMenu(){
        return menuService.fetchMenuList();
    }

    @GetMapping("/{day}")
    public Menu getMenuByDay(@PathVariable String day){
        Menu menu = menuService.findMenuByDay(day);
        return menu;
    }

    @PostMapping()
    public Menu saveMenu(@RequestBody Menu menu){
        return menuService.saveMenu(menu);
    }

    @PutMapping("/{id}")
    public Menu updateMenu(@RequestBody Menu menu, @PathVariable Long id){
        return menuService.updateMenu(menu,id);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuById(@PathVariable Long id){
        menuService.deleteMenuById(id);
    }
    @GetMapping("/getItems/{id}")
    public List<Item> getItemsOfMenuById(@PathVariable Long id){

        var i = menuService.getItemsOfAMenuById(id);
        return i;
    }

}
