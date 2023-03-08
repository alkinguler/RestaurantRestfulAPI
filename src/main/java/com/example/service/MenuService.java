package com.example.service;

import com.example.dao.ItemRepository;
import com.example.dao.MenuItemRepository;
import com.example.dao.MenuRepository;
import com.example.entity.Item;
import com.example.entity.Menu;
import com.example.entity.MenuItem;
import com.example.model.MenuModel;
import com.example.request.UpdateMenuRequest;
import com.example.response.GetMenuResponse;
import com.example.response.UpdateMenuResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ItemRepository itemRepository;

    private final MenuItemRepository menuItemRepository;
    public GetMenuResponse get() {
        List<String> days = menuRepository.findAll().stream().map(Menu::getDay).toList();
        List<MenuModel> menuModels = new ArrayList<>();
        List<List<MenuItem>> menuItems = menuRepository.findAll().stream().map(Menu::getMenuItems).toList();

       for (int i = 0; i < days.size(); i++){
           var newMenuModel = MenuModel.builder()
                   .menuItems(menuItems.get(i).stream().map(MenuItem::getItem).toList())
                   .day(days.get(i))
                   .build();
           menuModels.add(newMenuModel);
       }
        var object =  new GetMenuResponse();
       object.setMenuList(menuModels);
       return object;

    }


    public GetMenuResponse find(@PathVariable String day)
    {

        List<Long> menuIds = menuRepository.findMenuIdByDay(day);
        List<MenuModel> menuModels = new ArrayList<>();



        for (int i = 0; i < menuIds.size(); i++){
            List<List<MenuItem>> menuItems = menuRepository.findById(menuIds.get(i)).stream().map(Menu::getMenuItems).toList();

            MenuModel newMenuModel = MenuModel.builder()
                    .menuItems(menuItems.get(i).stream().map(MenuItem::getItem).toList())
                    .day(day)
                    .build();

            menuModels.add(newMenuModel);
        }
        var object =  new GetMenuResponse();
        object.setMenuList(menuModels);
        return object;
    }

    public void delete(Long id){
        itemRepository.deleteById(id);
    }

    public UpdateMenuResponse update(UpdateMenuRequest updateMenuRequest) {
        var menuId = updateMenuRequest.getMenuId();
        var itemIdList = updateMenuRequest.getItemIdList();
        List<Item> newItemList = new ArrayList<>();


        //Validation

        if(menuRepository.findById(menuId).isEmpty()){
            throw new EntityNotFoundException("Menu not found with id: " + menuId);
        }

        //TODO: Delete old relations will be implemented.

        for(Long itemId : itemIdList){
            var item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("Item not found with id: " + itemId));
            MenuItem menuItem = MenuItem.builder().item(item).menu_id(menuId).build();
            menuItemRepository.save(menuItem);
        }





        return null;
    }

}
