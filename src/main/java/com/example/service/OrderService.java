package com.example.service;

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
import com.example.response.GetOrderResponse;
import com.example.response.UpdateOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public CreateOrderResponse create(CreateOrderRequest request){
        OrderModel orderModel = request.getOrder();

        int TotalPrice = orderModel.getOrderItems()
                .stream()
                .mapToInt(
                        orderItem -> orderItem.getQuantity() * (itemRepository.findById(orderItem.getItem().getItemId()).get().getPrice())
                )
                .sum();

        Order order = Order.builder()
                .UserId(orderModel.getUserId())
                .Date(new Date())
                .TotalPrice(TotalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);

        orderModel.getOrderItems().forEach(
                orderItemModel -> {
                    Item item = itemRepository.findById(orderItemModel.getItem().getItemId()).get();

                    OrderItem orderItem = OrderItem.builder()
                            .Quantity(orderItemModel.getQuantity())
                            .OrderId(savedOrder.getId())
                            .Item(item)
                            .build();

                    orderItemRepository.save(orderItem);
                }
        );

        CreateOrderResponse response = CreateOrderResponse
                .builder()
                .createdOrder(orderModel)
                .build();

        return response;
    }

    public GetOrderResponse get(){
        return null;
    }

    public UpdateOrderResponse update(UpdateOrderRequest request){
        return null;
    }

    public void delete(Long id){
        return;
    }
}
