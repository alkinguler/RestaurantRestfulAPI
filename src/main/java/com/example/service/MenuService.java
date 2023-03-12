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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    @Autowired
    private final MenuRepository menuRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public CreateMenuResponse create(CreateMenuRequest createMenuRequest)
    {
        try {
            //Validation
            if (createMenuRequest.getDay() == null || createMenuRequest.getDay().isEmpty()) {
                throw new InvalidParameterException(Constants.DAY_NOT_FOUND_CONST);
            }

            if(!Constants.DAY_LIST_CONST.contains(StringUtils.capitalize(createMenuRequest.getDay())))
            {
                String errorMessage = String.format("'%s' %s",createMenuRequest.getDay(),Constants.WRONG_DAY_CONST);
                throw new InvalidParameterException(errorMessage);
            }


            //Define request info and necessary variables
            List<Long> itemIdList = createMenuRequest.getItemIdList();
            List<Item> newItemList = new ArrayList<>();

            //Update menu day in db if requested
            Menu savedMenu = menuRepository.save(Menu.builder().day(StringUtils.capitalize(createMenuRequest.getDay())).build());

            //If there is no requested items, just return the model without assign any relations in db.
            if(itemIdList == null || itemIdList.isEmpty()){
                MenuModel noItemMenuModel = MenuModel.builder().menuId(savedMenu.getId()).day(createMenuRequest.getDay()).build();
                return CreateMenuResponse.builder().createdMenu(noItemMenuModel).build();
            }


            //Create relations
            for(Long itemId : itemIdList){
                Item item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + itemId));
                newItemList.add(item);
                MenuItem menuItem = MenuItem.builder().item(item).menu(savedMenu).build();
                menuItemRepository.save(menuItem);
            }

            //Create menu response model
            MenuModel menuResponseModel = MenuModel.builder().menuId(savedMenu.getId()).menuItems(newItemList).day(StringUtils.capitalize(createMenuRequest.getDay())).build();

            return CreateMenuResponse.builder().createdMenu(menuResponseModel).build();
        }

        catch (InvalidParameterException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
    @Transactional(readOnly = true)
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
            responseModels.add(MenuModel.builder().menuId(menu.getId()).menuItems(menuItems).day(menu.getDay()).build());
        }

        return GetMenuResponse.builder().menuList(responseModels).build();

    }

    @Transactional(readOnly = true)
    public GetMenuResponse find(@PathVariable String day)
    {
        try {
            //Validation
            if(menuRepository.findMenuIdByDay(day).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_DAY_CONST + day);
            }

            List<Long> menuIds = menuRepository.findMenuIdByDay(day);
            List<MenuModel> menuModels = new ArrayList<>();

            //Find menu items and return them with respective date
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
                        .menuId(menuId)
                        .build();

                menuModels.add(newMenuModel);
            }

            return GetMenuResponse.builder().menuList(menuModels).build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @Transactional
    public UpdateMenuResponse update(UpdateMenuRequest updateMenuRequest) {
        try
        {
            //Validations
            if(menuRepository.findById(updateMenuRequest.getMenuId()).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + updateMenuRequest.getMenuId());
            }

            if(!Constants.DAY_LIST_CONST.contains(StringUtils.capitalize(updateMenuRequest.getDay())))
            {
                String errorMessage = String.format("'%s' %s",updateMenuRequest.getDay(),Constants.WRONG_DAY_CONST);
                throw new InvalidParameterException(
                        errorMessage
                );
            }

            //Define necessary elements
            Long menuId = updateMenuRequest.getMenuId();
            List<Long> newItemIdList = updateMenuRequest.getItemIdList();
            List<Item> newItemList = new ArrayList<>();
            List<Long> oldItemIdList = menuItemRepository
                    .findAll()
                    .stream()

                    .filter(menuItem-> menuItem.getMenu()
                            .getId()
                            .equals(menuId))
                    .map(menuItem->menuItem.getItem().getId())
                    .toList();

            //Get old menu by menu_id
            Menu oldMenu = menuRepository
                    .findById(updateMenuRequest.getMenuId())
                    .orElseThrow(
                            () -> new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + updateMenuRequest.getMenuId()));

            //If request day is not null, update it
            if(!updateMenuRequest.getDay().isEmpty())
            {
                oldMenu.setDay(updateMenuRequest.getDay());
                menuRepository.save(oldMenu);
            }


            //If old items and requested items are the same or new items are empty, break from the method.
            if(newItemIdList.equals(oldItemIdList) || newItemIdList.isEmpty()){
                List<Item> oldItemList = itemRepository.findAll()
                        .stream()
                        .filter(item -> oldItemIdList.contains(item.getId()))
                        .toList();

                return UpdateMenuResponse.builder()
                        .updatedMenu(MenuModel.builder()
                                .menuId(oldMenu.getId())
                                .day(updateMenuRequest.getDay())
                                .menuItems(oldItemList).build())
                        .build();

            }


            List<MenuItem> oldItems = menuItemRepository
                    .findAll()
                    .stream()
                    .filter(menuItem -> menuItem.getMenu().getId().equals(menuId)).toList();

            //If there exists old items, delete them.
            if(!oldItems.isEmpty())
            {
                for (var oldItem : oldItems){
                    menuItemRepository.deleteById(oldItem.getId());
                }
            }

            //Add new relations
            for(Long itemId : newItemIdList){
                Item item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + itemId));
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

            MenuModel responseMenuModel = MenuModel.builder().menuId(oldMenu.getId()).menuItems(newItemList).day(updateMenuRequest.getDay()).build();

            return UpdateMenuResponse.builder().updatedMenu(responseMenuModel).build();

        }

        catch (EntityNotFoundException | InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }

    }

    @Transactional
    public void delete(Long id){
        try {
            //Validation
            if(menuRepository.findById(id).isEmpty()){
                throw new EntityNotFoundException(Constants.MENU_NOT_FOUND_BY_ID_CONST + id);
            }
            //Get menu-item relations
            List<MenuItem> menuItems = menuItemRepository.findAll().stream().filter(menuItem -> menuItem.getMenu().getId().equals(id)).toList();

            //If any relations exist, delete them
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



}
