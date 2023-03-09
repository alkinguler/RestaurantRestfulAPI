package com.example.service;

import com.example.dao.ItemRepository;
import com.example.dao.OrderItemRepository;
import com.example.dao.OrderRepository;
import com.example.entity.Item;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.model.*;
import com.example.request.CreateOrderRequest;
import com.example.request.GetOrderRequest;
import com.example.request.UpdateOrderRequest;
import com.example.response.CreateOrderResponse;
import com.example.response.GetOrderResponse;
import com.example.response.UpdateOrderResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                .userId(orderModel.getUserId())
                .date(new Date())
                .TotalPrice(TotalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);

        orderModel.getOrderItems().forEach(
                orderItemModel -> {
                    Item item = itemRepository.findById(orderItemModel.getItem().getItemId()).get();

                    OrderItem orderItem = OrderItem.builder()
                            .quantity(orderItemModel.getQuantity())
                            .order(savedOrder)
                            .item(item)
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
        List<Order> orders = orderRepository.findAll();
        List<OrderModel> orderModels = new ArrayList<>();


        return null;
    }

    public UpdateOrderResponse update(UpdateOrderRequest request)
    {

        Order oldOrder = orderRepository.findById(request.getOrderId()).orElseThrow(()-> new EntityNotFoundException("Order not found by id: " + request.getOrderId()));

        List<Item> newItemList = itemRepository.findAll()
                .stream().
                filter(e->request.getItemIds().contains(e.getId()))
                .toList();

        //Delete old OrderItem relations
        List<OrderItem> oldRelations = orderItemRepository.findAll()
                .stream()
                .filter(e->e.getOrder().getId().equals(request.getOrderId()))
                .toList();
//        for(OrderItem relation : oldRelations){
//            orderItemRepository.deleteById(relation.getId());
//        }

        return null;
    }

    public void delete(Long id){
        return;
    }

    public GetOrderResponse find(GetOrderRequest orderRequest){

        return null;
    }
}
