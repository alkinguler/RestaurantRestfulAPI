package com.example.controller;

import com.example.model.Menu;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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


    @GetMapping("/getMenuById/{id}")
    public ResponseEntity<Optional<Menu>> getMenuById(@PathVariable Long id){
        var result = menuService.findMenuById(id);
        HttpStatus httpStatus = HttpStatus.OK;

        if(result.isEmpty()){
           httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result,httpStatus) ;
    }

    @GetMapping("/getMenuByDay/{day}")
    public Menu getMenuByDay(@PathVariable String day){
        Menu menu = menuService.findMenuByDay(day);
        return menu;
    }

    @PostMapping()
    public ResponseEntity<Menu> saveMenu(@RequestBody Menu menu){
        return new ResponseEntity<Menu>(menuService.saveMenu(menu), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public Menu updateMenu(@RequestBody Menu menu, @PathVariable Long id){
        return menuService.updateMenu(menu,id);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuById(@PathVariable Long id){
        menuService.deleteMenuById(id);
    }


}
