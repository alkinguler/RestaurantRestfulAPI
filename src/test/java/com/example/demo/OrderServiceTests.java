package com.example.demo;

import com.example.dao.ItemRepository;
import com.example.dao.OrderItemRepository;
import com.example.dao.OrderRepository;
import com.example.entity.Item;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.model.ItemQuantityModel;
import com.example.model.OrderFetchModel;
import com.example.model.OrderItemModel;
import com.example.model.OrderUpdateModel;
import com.example.request.UpdateOrderRequest;
import com.example.response.FindOrderResponse;
import com.example.response.GetOrderResponse;
import com.example.response.UpdateOrderResponse;
import com.example.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService testService;

    @Test
    void testGetOrder(){
        List<Order> orders = Collections.singletonList(Order.builder().id(1L).date(new Date()).userId(123L).TotalPrice(3).build());
        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("name").build());
        List<ItemQuantityModel> itemQuantityModels =
                Collections.singletonList(ItemQuantityModel.builder().quantity(1).item(items.get(0)).build());
        List<OrderFetchModel> orderFetchModels = Collections.singletonList(OrderFetchModel.builder().itemQuantityModels(itemQuantityModels)
                .OrderId(orders.get(0).getId()).UserId(orders.get(0).getUserId()).build());
        List<OrderItem> orderItems = Collections.singletonList(OrderItem.builder().order(orders.get(0)).quantity(itemQuantityModels.get(0).getQuantity()).item(items.get(0)).id(1L).build());

        GetOrderResponse expectedResponse = GetOrderResponse.builder().orders(orderFetchModels).build();


        when(orderRepository.findAll()).thenReturn(orders);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        GetOrderResponse actualResponse = testService.get();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actualResponse),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getItemQuantityModels().get(0).getItem(),actualResponse.getOrders().get(0).getItemQuantityModels().get(0).getItem()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getItemQuantityModels().get(0).getQuantity(),actualResponse.getOrders().get(0).getItemQuantityModels().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getOrderId(),actualResponse.getOrders().get(0).getOrderId()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getUserId(),actualResponse.getOrders().get(0).getUserId())
        );

    }

    @Test
    void testFindOrder(){
        Order order = Order.builder().id(1L).date(new Date()).userId(123L).TotalPrice(3).build();
        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("name").build());
        List<OrderItem> orderItems = Collections.singletonList(OrderItem.builder().quantity(1).order(order).item(items.get(0)).id(1L).build());
        List<ItemQuantityModel> itemQuantityModels = Collections.singletonList(ItemQuantityModel.builder().item(items.get(0)).quantity(orderItems.get(0).getQuantity()).build());
        OrderFetchModel orderFetchModels = OrderFetchModel.builder().UserId(order.getUserId()).OrderId(order.getId()).itemQuantityModels(itemQuantityModels).build();
        FindOrderResponse expectedResponse =
                FindOrderResponse.builder().orderFetchModel(orderFetchModels).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        FindOrderResponse actualResponse = testService.find(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getOrderId(),actualResponse.getOrderFetchModel().getOrderId()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getUserId(),actualResponse.getOrderFetchModel().getUserId()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getItemQuantityModels().get(0).getQuantity(),actualResponse.getOrderFetchModel().getItemQuantityModels().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getItemQuantityModels().get(0).getItem(),actualResponse.getOrderFetchModel().getItemQuantityModels().get(0).getItem()),
                () -> Assertions.assertThrows(ResponseStatusException.class,()-> testService.find(2L))
        );
    }

    @Test
    void testUpdateOrder(){

        List<OrderItemModel> orderItemModels = Collections.singletonList(OrderItemModel.builder().item_id(1L).quantity(1).build());
        Item item = Item.builder().price(10).name("testItem").id(1L).build();
        UpdateOrderRequest updateOrderRequest = UpdateOrderRequest
                .builder()
                .orderId(1L)
                .orderItemModels(orderItemModels)
                .build();

        UpdateOrderRequest updateOrderErrorRequest = UpdateOrderRequest
                .builder()
                .orderId(2L)
                .orderItemModels(orderItemModels)
                .build();

        List<ItemQuantityModel> itemQuantityModels =
                Collections.singletonList(ItemQuantityModel
                        .builder()
                        .quantity(
                                orderItemModels.get(0)
                                        .getQuantity())
                        .item(item)
                        .build());

        List<Order> orders =
                Collections.singletonList(
                        Order.builder()
                                .id(1L)
                                .TotalPrice(10)
                                .userId(123L)
                                .date(new Date())
                                .build()
                );

        List<OrderItem> orderItems =
                Collections.singletonList(
                        OrderItem
                                .builder()
                                .item(item)
                                .order(orders.get(0))
                                .id(1L)
                                .quantity(
                                        orderItemModels.get(0)
                                        .getQuantity()
                                )
                        .build());
        OrderUpdateModel orderUpdateModel =
                OrderUpdateModel.
                        builder()
                        .user_id(orders.get(0).getUserId())
                        .updatedOrderItems(itemQuantityModels)
                        .order_id(orders.get(0).getId())
                        .build();
        UpdateOrderResponse expectedResponse = UpdateOrderResponse.builder().updatedOrder(orderUpdateModel).build();



        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(orders.get(0)));
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        when(orderItemRepository.findAll()).thenReturn(orderItems);
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(orderRepository.save(any())).thenReturn(orders.get(0));



        var actualResponse = testService.update(updateOrderRequest);


        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getItem(),actualResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getItem()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getQuantity(),actualResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getOrder_id(),actualResponse.getUpdatedOrder().getOrder_id()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUser_id(),actualResponse.getUpdatedOrder().getUser_id()),
                () -> Assertions.assertThrows(ResponseStatusException.class,() -> testService.update(updateOrderErrorRequest))
        );


    }


}
