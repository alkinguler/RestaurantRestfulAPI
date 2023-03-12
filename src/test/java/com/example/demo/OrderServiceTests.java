package com.example.demo;

import com.example.dao.ItemRepository;
import com.example.dao.OrderItemRepository;
import com.example.dao.OrderRepository;
import com.example.entity.Item;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.model.*;
import com.example.request.CreateOrderRequest;
import com.example.request.UpdateOrderRequest;
import com.example.response.CreateOrderResponse;
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
    void testGetOrder() {
        List<Order> orders = Collections.singletonList(Order.builder().id(1L).date(new Date()).userId(123L).totalPrice(3).build());
        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("name").build());
        List<ItemQuantityModel> itemQuantityModels =
                Collections.singletonList
                        (
                                ItemQuantityModel
                                        .builder()
                                        .quantity(1)
                                        .item(items.get(0))
                                        .build()
                        );
        List<OrderFetchModel> orderFetchModels =
                Collections.singletonList(
                        OrderFetchModel
                                .builder()
                                .itemQuantityModels(itemQuantityModels)
                                .orderId(orders.get(0).getId()).userId(orders.get(0).getUserId())
                                .build()
                );
        List<OrderItem> orderItems =
                Collections.singletonList
                        (
                                OrderItem
                                        .builder()
                                        .order(orders.get(0))
                                        .quantity(itemQuantityModels.get(0).getQuantity())
                                        .item(items.get(0))
                                        .id(1L)
                                        .build()
                        );
        GetOrderResponse expectedResponse = GetOrderResponse.builder().orders(orderFetchModels).build();


        when(orderRepository.findAll()).thenReturn(orders);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        GetOrderResponse actualResponse = testService.get();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actualResponse),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getItemQuantityModels().get(0).getItem(), actualResponse.getOrders().get(0).getItemQuantityModels().get(0).getItem()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getItemQuantityModels().get(0).getQuantity(), actualResponse.getOrders().get(0).getItemQuantityModels().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getOrderId(), actualResponse.getOrders().get(0).getOrderId()),
                () -> Assertions.assertEquals(expectedResponse.getOrders().get(0).getUserId(), actualResponse.getOrders().get(0).getUserId())
        );

    }

    @Test
    void testFindOrder() {

        Order order = Order.builder().id(1L).date(new Date()).userId(123L).totalPrice(3).build();
        List<Item> items = Collections.singletonList(Item.builder().id(1L).name("name").build());
        List<OrderItem> orderItems =
                Collections.singletonList
                        (
                                OrderItem
                                        .builder()
                                        .quantity(1)
                                        .order(order)
                                        .item(items.get(0))
                                        .id(1L)
                                        .build()
                        );

        List<ItemQuantityModel> itemQuantityModels =
                Collections.singletonList
                        (
                                ItemQuantityModel
                                        .builder()
                                        .item(items.get(0))
                                        .quantity(orderItems.get(0).getQuantity())
                                        .build()
                        );

        OrderFetchModel orderFetchModels = OrderFetchModel
                .builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .itemQuantityModels(itemQuantityModels)
                .build();

        FindOrderResponse expectedResponse =
                FindOrderResponse.builder().orderFetchModel(orderFetchModels).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        FindOrderResponse actualResponse = testService.find(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getOrderId(), actualResponse.getOrderFetchModel().getOrderId()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getUserId(), actualResponse.getOrderFetchModel().getUserId()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getItemQuantityModels().get(0).getQuantity(), actualResponse.getOrderFetchModel().getItemQuantityModels().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getOrderFetchModel().getItemQuantityModels().get(0).getItem(), actualResponse.getOrderFetchModel().getItemQuantityModels().get(0).getItem()),
                () -> Assertions.assertThrows(ResponseStatusException.class, () -> testService.find(2L))
        );
    }

    @Test
    void testUpdateOrder() {

        List<OrderItemModel> orderItemModels = Collections.singletonList(OrderItemModel.builder().itemId(1L).quantity(1).build());

        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
        updateOrderRequest.setOrderId(1L);
        updateOrderRequest.setOrderItemModels(orderItemModels);

        UpdateOrderRequest updateOrderErrorRequest = new UpdateOrderRequest();
        updateOrderErrorRequest.setOrderId(2L);
        updateOrderErrorRequest.setOrderItemModels(orderItemModels);

        Item item = Item.builder().price(10).name("testItem").id(1L).build();

        List<ItemQuantityModel> itemQuantityModels =
                Collections.singletonList(
                        ItemQuantityModel.builder().quantity(orderItemModels.get(0).getQuantity()).item(item).build()
                );

        List<Order> orders =
                Collections.singletonList(
                        Order.builder().id(1L).totalPrice(10).userId(123L).date(new Date()).build()
                );

        List<OrderItem> orderItems =
                Collections.singletonList(
                        OrderItem
                                .builder()
                                .item(item)
                                .order(orders.get(0))
                                .id(1L)
                                .quantity(orderItemModels.get(0).getQuantity())
                                .build()
                );
        OrderUpdateModel orderUpdateModel =
                OrderUpdateModel
                        .builder().userId(orders.get(0).getUserId())
                        .updatedOrderItems(itemQuantityModels)
                        .orderId(orders.get(0).getId())
                        .totalPrice(itemQuantityModels.get(0).getQuantity()*itemQuantityModels.get(0).getItem().getPrice())
                        .build();
        UpdateOrderResponse expectedResponse = UpdateOrderResponse.builder().updatedOrder(orderUpdateModel).build();


        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(orders.get(0)));
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        when(orderItemRepository.findAll()).thenReturn(orderItems);
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(orderRepository.save(any())).thenReturn(orders.get(0));


        UpdateOrderResponse actualResponse = testService.update(updateOrderRequest);


        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getItem(), actualResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getItem()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getQuantity(), actualResponse.getUpdatedOrder().getUpdatedOrderItems().get(0).getQuantity()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getTotalPrice(), actualResponse.getUpdatedOrder().getTotalPrice()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getOrderId(), actualResponse.getUpdatedOrder().getOrderId()),
                () -> Assertions.assertEquals(expectedResponse.getUpdatedOrder().getUserId(), actualResponse.getUpdatedOrder().getUserId()),
                () -> Assertions.assertThrows(ResponseStatusException.class, () -> testService.update(updateOrderErrorRequest))
        );


    }

    @Test
    void testCreateOrder() {
        Item item = Item.builder().name("testItem").id(1L).price(10).build();
        List<OrderItemModel> orderItemModels = Collections.singletonList(
                OrderItemModel.builder().itemId(item.getId()).quantity(2).build()
        );



        OrderModel orderModel = OrderModel.builder().userId(123L).orderItems(orderItemModels).build();
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setOrder(orderModel);

        Order newOrder = Order
                .builder()
                .userId(orderRequest.getOrder().getUserId())
                .id(1L)
                .totalPrice(orderItemModels.get(0)
                        .getQuantity()*10)
                .build();

        List<ItemQuantityModel> itemQuantityModels = Collections.singletonList(
                ItemQuantityModel.builder().quantity(2).item(item).build()
        );
        CreatedOrderModel createdOrderModel = CreatedOrderModel
                .builder()
                .orderedItems(itemQuantityModels)
                .userId(orderModel.getUserId())
                .orderId(newOrder.getId())
                .totalPrice(newOrder.getTotalPrice())
                .build();
        CreateOrderResponse expectedResponse = CreateOrderResponse.builder().createdOrder(createdOrderModel).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any())).thenReturn(newOrder);

        CreateOrderResponse actualResponse = testService.create(orderRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actualResponse.getCreatedOrder()),
                () -> Assertions.assertNotNull(actualResponse.getCreatedOrder().getOrderId()),
                () -> Assertions.assertEquals(actualResponse.getCreatedOrder().getOrderId(),newOrder.getId()),
                () -> Assertions.assertEquals(
                        expectedResponse.getCreatedOrder().getOrderedItems().get(0).getItem(),
                        actualResponse.getCreatedOrder().getOrderedItems().get(0).getItem()
                ),
                () -> Assertions.assertEquals(
                        expectedResponse.getCreatedOrder().getOrderedItems().get(0).getQuantity(),
                        actualResponse.getCreatedOrder().getOrderedItems().get(0).getQuantity()
                ),
                () -> Assertions.assertEquals(expectedResponse.getCreatedOrder().getUserId(),actualResponse.getCreatedOrder().getUserId()),
                () -> Assertions.assertEquals(expectedResponse.getCreatedOrder().getTotalPrice(),actualResponse.getCreatedOrder().getTotalPrice())
        );
    }


}
