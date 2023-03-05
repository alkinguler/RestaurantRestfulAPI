package com.example.service;

import com.example.dao.MenuRepository;
import com.example.model.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public List<Menu> fetchMenuList() {
        return menuRepository.findAll();
    }

    public Menu findMenuByDay(String day){
        return menuRepository.findByDay(day);
    }

    public Menu updateMenu(Menu newMenu, Long menuId) {
        return menuRepository.findById(menuId).map(menu -> {
            menu.setDay(newMenu.getDay());
            return menuRepository.save(newMenu);
        }).orElseGet(() -> {
            newMenu.setId(menuId);
            return menuRepository.save(newMenu);
        });
    }


    public void deleteMenuById(Long menuId){
        menuRepository.deleteById(menuId);
    }

    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

}