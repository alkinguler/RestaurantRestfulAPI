package com.example.demo;

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
import com.example.service.MenuService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MenuServiceTests {

    @Mock(name = "menuRepository")
    public MenuRepository menuRepository;
    @Mock
    public MenuItemRepository menuItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    MenuService testService;


    @Test
    void testGetMenu() {

        Menu menu = Menu.builder().day("test").id(1L).build();
        List<Menu> menuList = new ArrayList<>();
        menuList.add(menu);

        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("name").build());
        MenuModel menuModel = MenuModel.builder().menuItems(items).day("test").build();
        List<MenuModel> menuModelList = Collections.singletonList(menuModel);

        MenuItem menuItem = MenuItem.builder().item(items.get(0)).menu(menuList.get(0)).id(1L).build();
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(menuItem);

        GetMenuResponse mockResponse = GetMenuResponse.builder().menuList(menuModelList).build();
        when(menuRepository.findAll()).thenReturn(menuList);
        when(menuItemRepository.findAll()).thenReturn(menuItems);

        GetMenuResponse actualResponse = testService.get();

        Assertions.assertAll(
            () -> Assertions.assertNotNull(actualResponse),
            () -> Assertions.assertEquals(actualResponse.getMenuList().get(0).getDay(), mockResponse.getMenuList().get(0).getDay()),
            () -> Assertions.assertEquals(actualResponse.getMenuList().get(0).getMenuItems(), mockResponse.getMenuList().get(0).getMenuItems())
        );

    }

    @Test
    void testFindMenu() {

        Menu menu = Menu.builder().day("Monday").id(1L).build();
        List<Menu> menuList = Collections.singletonList(menu);

        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("test").build());
        MenuModel menuModel = MenuModel.builder().menuItems(items).day("Monday").build();
        List<MenuModel> menuModelList = Collections.singletonList(menuModel);

        MenuItem menuItem = MenuItem.builder().item(items.get(0)).menu(menuList.get(0)).id(1L).build();
        List<MenuItem> menuItems = Collections.singletonList(menuItem);

        GetMenuResponse mockResponse = GetMenuResponse.builder().menuList(menuModelList).build();

        when(menuItemRepository.findAll()).thenReturn(menuItems);
        when(menuRepository.findMenuIdByDay("Monday")).thenReturn(Collections.singletonList(menu.getId()));

        GetMenuResponse actualResponse = testService.find("Monday");
        Assertions.assertAll(
            () -> Assertions.assertNotNull(actualResponse),
            () -> Assertions.assertEquals(actualResponse.getMenuList().get(0).getDay(), mockResponse.getMenuList().get(0).getDay()),
            () -> Assertions.assertEquals(actualResponse.getMenuList().get(0).getMenuItems(), mockResponse.getMenuList().get(0).getMenuItems()),
            () -> Assertions.assertThrows(ResponseStatusException.class, () -> testService.find("exception"))
        );

    }

    @Test
    void testUpdateMenu() {
        UpdateMenuRequest request = new UpdateMenuRequest();
        request.setMenuId(1L);
        request.setDay("Monday");
        request.setItemIdList(Collections.singletonList(2L));

        UpdateMenuRequest errorRequest = new UpdateMenuRequest();
        errorRequest.setMenuId(3L);
        errorRequest.setDay("Monday");
        errorRequest.setItemIdList(Collections.singletonList(2L));

        List<Item> currentItems =
            Collections.singletonList(Item.builder().price(1).id(1L).name("testItem").build());
        List<Item> newItems =
            Collections.singletonList(Item.builder().price(1).id(2L).name("testItem2").build());

        MenuModel menuModel = MenuModel.builder().menuItems(newItems).day("Monday").build();
        Menu x = Menu.builder().day("Monday").id(1L).build();
        MenuItem menuItem = MenuItem.builder().item(currentItems.get(0)).menu(x).id(1L).build();
        UpdateMenuResponse expectedResponse = UpdateMenuResponse.builder().updatedMenu(menuModel).build();

        when(menuRepository.findById(1L)).thenReturn(Optional.ofNullable(x));
        when(menuRepository.findById(3L)).thenReturn(Optional.empty());
        when(menuItemRepository.findAll())
            .thenReturn(Collections.singletonList(menuItem));
        when(itemRepository.findById(2L)).thenReturn(Optional.ofNullable(newItems.get(0)));


        UpdateMenuResponse actualResponse = testService.update(request);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(actualResponse.getUpdatedMenu()),
            () -> Assertions.assertEquals(actualResponse.getUpdatedMenu().getMenuItems(), expectedResponse.getUpdatedMenu().getMenuItems()),
            () -> Assertions.assertEquals(actualResponse.getUpdatedMenu().getDay(), expectedResponse.getUpdatedMenu().getDay()),
            () -> Assertions.assertThrows(ResponseStatusException.class, () -> testService.update(errorRequest))
        );

    }

    @Test
    void testCreateMenu() {
        Item item = Item.builder().name("testItem").price(3).id(1L).build();
        List<Long> itemIdList = Collections.singletonList(1L);
        List<Long> emptyItemIdList = Collections.emptyList();
        CreateMenuRequest request = new CreateMenuRequest();
        request.setItemIdList(itemIdList);
        request.setDay("Monday");

        CreateMenuRequest emptyItemRequest = new CreateMenuRequest();
        emptyItemRequest.setItemIdList(emptyItemIdList);
        emptyItemRequest.setDay("Monday");
        

        Menu createdMenu = Menu.builder().id(1L).day("Monday").build();
        MenuModel expectedMenuModel = MenuModel.builder().menuId(createdMenu.getId()).day(request.getDay()).menuItems(Collections.singletonList(item)).build();
        CreateMenuResponse expectedResponse = CreateMenuResponse.builder().createdMenu(expectedMenuModel).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(menuRepository.save(any())).thenReturn(createdMenu);

        CreateMenuResponse actualResponse = testService.create(request);
        CreateMenuResponse actualNoItemResponse = testService.create(emptyItemRequest);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(actualResponse.getCreatedMenu()),
            () -> Assertions.assertNotNull(actualResponse.getCreatedMenu().getMenuId()),
            () -> Assertions.assertEquals(expectedResponse.getCreatedMenu().getMenuId(), actualResponse.getCreatedMenu().getMenuId()),
            () -> Assertions.assertEquals(expectedResponse.getCreatedMenu().getDay(), actualResponse.getCreatedMenu().getDay()),
            () -> Assertions.assertEquals(expectedResponse.getCreatedMenu().getMenuItems(), actualResponse.getCreatedMenu().getMenuItems()),
            ()-> Assertions.assertNull(actualNoItemResponse.getCreatedMenu().getMenuItems())
        );
    }

}
