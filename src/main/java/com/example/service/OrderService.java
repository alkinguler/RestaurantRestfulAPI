package com.example.service;

import com.example.dao.ItemRepository;
import com.example.dao.MenuRepository;
import com.example.dao.OrderItemRepository;
import com.example.dao.OrderRepository;
import com.example.model.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public Order createOrder(OrderDto orderDto){
        Order order = new Order();
        order.setUserId(orderDto.getUserId());
        order.setDate(new Date());
        Integer totalPrice = 0;

        for (OrderItemDto orderItemDto : orderDto.getOrderItems())
        {
            totalPrice += itemRepository.getItemPriceById(orderItemDto.getItemId()) * orderItemDto.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems;

        for (OrderItemDto orderItemDto : orderDto.getOrderItems()){
                Item item = itemRepository.findById(orderItemDto.getItemId()).orElseThrow(
                        () -> new EntityNotFoundException("Item not found with Id: " + orderItemDto.getItemId())
                );

                OrderItem orderItem = new OrderItem();
               orderItem.setItemId(item.getId());
               orderItem.setQuantity(orderItemDto.getQuantity());
               orderItem.setOrderId(savedOrder.getId());
               orderItemRepository.save(orderItem);
        }

        return savedOrder;
    }

}
