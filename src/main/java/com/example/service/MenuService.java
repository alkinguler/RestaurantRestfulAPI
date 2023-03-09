package com.example.service;

import com.example.dao.ItemRepository;
import com.example.dao.MenuItemRepository;
import com.example.dao.MenuRepository;
import com.example.entity.Item;
import com.example.entity.Menu;
import com.example.entity.MenuItem;
import com.example.model.MenuModel;
import com.example.request.CreateMenuRequest;
import com.example.request.UpdateMenuRequest;
import com.example.response.CreateMenuResponse;
import com.example.response.GetMenuResponse;
import com.example.response.UpdateMenuResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
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
        try {
            if(menuRepository.findById(id).isEmpty()){
                throw new EntityNotFoundException("Menu not found with id: " + id);
            }
            List<MenuItem> menuItems = menuRepository.findById(id).stream().map(Menu::getMenuItems).flatMap(Collection::stream).filter(e->e.getMenu().getId().equals(id)).toList();

            if(!menuItems.isEmpty())
            {
                for (var menuItem : menuItems){
                    menuItemRepository.deleteById(menuItem.getId());
                }
            }
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        catch (Exception e) {
            throw e;
        }

        menuRepository.deleteById(id);
    }

    public UpdateMenuResponse update(UpdateMenuRequest updateMenuRequest) {
        try{
            var menuId = updateMenuRequest.getMenuId();
            var itemIdList = updateMenuRequest.getItemIdList();
            List<Item> newItemList = new ArrayList<>();


            //Validation

            if(menuRepository.findById(menuId).isEmpty()){
                throw new EntityNotFoundException("Menu not found with id: " + menuId);
            }

            //Delete old relations
            List<MenuItem> oldItems = menuRepository.findById(menuId).stream().map(e->e.getMenuItems()).flatMap(Collection::stream).filter(e->e.getMenu().getId().equals(menuId)).toList();

            if(!oldItems.isEmpty())
            {
                for (var oldItem : oldItems){
                    menuItemRepository.deleteById(oldItem.getId());
                }
            }

            //Add new relations

            for(Long itemId : itemIdList){
                var item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("Item not found with id: " + itemId));
                newItemList.add(item);
                MenuItem menuItem = MenuItem.builder().item(item).menu(menuRepository.findById(menuId).get()).build();
                menuItemRepository.save(menuItem);
            }

            MenuModel responseMenuModel = MenuModel.builder().menuItems(newItemList).day(updateMenuRequest.getDay()).build();

            return UpdateMenuResponse.builder().UpdatedMenu(responseMenuModel).build();

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        catch (Exception e){
            throw e;
        }

    }

    public CreateMenuResponse create(CreateMenuRequest createMenuRequest)
    {

        List<Long> itemIdList = createMenuRequest.getItemIdList();
        List<Item> newItemList = new ArrayList<>();

        Menu savedMenu = menuRepository.save(Menu.builder().day(StringUtils.capitalize(createMenuRequest.getDay())).build());

        for(Long itemId : itemIdList){
            var item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("Item not found with id: " + itemId));
            newItemList.add(item);
            MenuItem menuItem = MenuItem.builder().item(item).menu(savedMenu).build();
            menuItemRepository.save(menuItem);
        }

        MenuModel menuResponseModel = MenuModel.builder().menuItems(newItemList).day(StringUtils.capitalize(createMenuRequest.getDay())).build();
        CreateMenuResponse createdMenuResponse = CreateMenuResponse.builder().createdMenu(menuResponseModel).build();
        return createdMenuResponse;
    }

}
