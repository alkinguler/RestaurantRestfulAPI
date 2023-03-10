package com.example.service;

import com.example.Constants;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ItemRepository itemRepository;

    private final MenuItemRepository menuItemRepository;
    public GetMenuResponse get() {

        List<Menu> allMenus = menuRepository.findAll();
        List<MenuModel> responseModels = new ArrayList<>();

        for(var menu:allMenus){
            List<Item> menuItems = menuItemRepository.findAll()
                    .stream()
                    .filter(
                            menuItem -> menuItem.getMenu().getId().equals(menu.getId())
                    )
                    .map(MenuItem::getItem)
                    .toList();
            responseModels.add(MenuModel.builder().menuItems(menuItems).day(menu.getDay()).build());
        }
        var response = new GetMenuResponse();
        response.setMenuList(responseModels);
        return response;

    }


    public GetMenuResponse find(@PathVariable String day)
    {
        try {
            //Validation
            if(menuRepository.findMenuIdByDay(day).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_DAY_CONST + day);
            }

            List<Long> menuIds = menuRepository.findMenuIdByDay(day);
            List<MenuModel> menuModels = new ArrayList<>();


            for (var menuId : menuIds){
                List<Item> menuItems = menuItemRepository.findAll()
                        .stream()
                        .filter(
                                menuItem -> menuIds.contains(menuItem.getMenu().getId()) && menuItem.getMenu().getId().equals(menuId)
                        )
                        .map(MenuItem::getItem)
                        .toList();

                MenuModel newMenuModel = MenuModel.builder()
                        .menuItems(menuItems)
                        .day(day)
                        .build();

                menuModels.add(newMenuModel);
            }
            var object =  new GetMenuResponse();
            object.setMenuList(menuModels);
            return object;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public void delete(Long id){
        try {
            if(menuRepository.findById(id).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + id);
            }
            List<MenuItem> menuItems = menuItemRepository.findAll().stream().filter(menuItem -> menuItem.getMenu().getId().equals(id)).toList();

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

        menuRepository.deleteById(id);
    }

    public UpdateMenuResponse update(UpdateMenuRequest updateMenuRequest) {
        try
        {
            if(menuRepository.findById(updateMenuRequest.getMenuId()).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + updateMenuRequest.getMenuId());
            }

            Long menuId = updateMenuRequest.getMenuId();
            List<Long> newItemIdList = updateMenuRequest.getItemIdList();
            List<Item> newItemList = new ArrayList<>();
            List<Long> oldItemIdList = menuItemRepository
                    .findAll()
                    .stream()

                    .filter(menuItem-> menuItem.getMenu()
                            .getId()
                            .equals(menuId))
                    .map(menuItem->menuItem.getItem().getId()).toList();

            //Get old menu by menu_id
            var oldMenu = menuRepository
                    .findById(updateMenuRequest.getMenuId())
                    .orElseThrow(
                            () -> new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + updateMenuRequest.getMenuId()));

            //if request body day is not null, update it
            if(!updateMenuRequest.getDay().isEmpty())
            {
                oldMenu.setDay(updateMenuRequest.getDay());
                menuRepository.save(oldMenu);
            }


            //if old items and requested items are the same or new items are empty, break from the method.
            if(newItemIdList.equals(oldItemIdList) || newItemIdList.isEmpty()){
                List<Item> oldItemList = itemRepository.findAll()
                        .stream()
                        .filter(item -> oldItemIdList.contains(item.getId()))
                        .toList();

                return UpdateMenuResponse.builder()
                        .UpdatedMenu(MenuModel.builder()
                        .day(updateMenuRequest.getDay())
                        .menuItems(oldItemList).build())
                        .build();

            }


            List<MenuItem> oldItems = menuItemRepository
                    .findAll()
                    .stream()
                    .filter(menuItem -> menuItem.getMenu().getId().equals(menuId)).toList();


            if(!oldItems.isEmpty())
            {
                for (var oldItem : oldItems){
                    menuItemRepository.deleteById(oldItem.getId());
                }
            }

            //Add new relations

            for(Long itemId : newItemIdList){
                var item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + itemId));
                newItemList.add(item);
                MenuItem menuItem = MenuItem
                        .builder()
                        .item(item)
                        .menu(
                            menuRepository.findById(menuId).orElseThrow
                            (
                                ()-> new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST)
                            )
                        )
                        .build();
                menuItemRepository.save(menuItem);
            }

            MenuModel responseMenuModel = MenuModel.builder().menuItems(newItemList).day(updateMenuRequest.getDay()).build();

            return UpdateMenuResponse.builder().UpdatedMenu(responseMenuModel).build();

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }

    }

    public CreateMenuResponse create(CreateMenuRequest createMenuRequest)
    {

        List<Long> itemIdList = createMenuRequest.getItemIdList();
        List<Item> newItemList = new ArrayList<>();

        Menu savedMenu = menuRepository.save(Menu.builder().day(StringUtils.capitalize(createMenuRequest.getDay())).build());

        for(Long itemId : itemIdList){
            var item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + itemId));
            newItemList.add(item);
            MenuItem menuItem = MenuItem.builder().item(item).menu(savedMenu).build();
            menuItemRepository.save(menuItem);
        }

        MenuModel menuResponseModel = MenuModel.builder().menuItems(newItemList).day(StringUtils.capitalize(createMenuRequest.getDay())).build();
        return CreateMenuResponse.builder().createdMenu(menuResponseModel).build();
    }

}
