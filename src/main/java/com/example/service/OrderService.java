package com.example.service;

import com.example.Constants;
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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
                        orderItem -> orderItem.getQuantity() * (itemRepository.findById(orderItem.getItem_id())
                                .orElseThrow(()->new RuntimeException(Constants.ITEM_NOT_FOUND_CONST + orderItem.getItem_id()))
                                .getPrice())
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
                    Item item = itemRepository.findById(orderItemModel.getItem_id()).orElseThrow();

                    OrderItem orderItem = OrderItem.builder()
                            .quantity(orderItemModel.getQuantity())
                            .order(savedOrder)
                            .item(item)
                            .build();

                    orderItemRepository.save(orderItem);
                }
        );

        return CreateOrderResponse
                .builder()
                .createdOrder(orderModel)
                .build();
    }

    public GetOrderResponse get(){
        var orders = orderRepository.findAll();
        List<OrderFetchModel> orderFetchResponseModels = new ArrayList<>();

        for(var order : orders){

            List<OrderItem> b = orderItemRepository.findAll().stream().filter(x->x.getOrder().getId().equals(order.getId())).toList();
            List<ItemQuantityModel> itemQuantityModels = new ArrayList<>();

            for(var orderItem : b){

                ItemQuantityModel itemQuantityModel = ItemQuantityModel.builder()
                        .item(orderItem.getItem())
                        .quantity(orderItem.getQuantity())
                        .build();

                itemQuantityModels.add(itemQuantityModel);

            }
            orderFetchResponseModels.add(
                    OrderFetchModel.builder()
                            .itemQuantityModels(itemQuantityModels)
                            .OrderId(order.getId())
                            .UserId(order.getUserId())
                            .build());
        }

        return GetOrderResponse
                .builder()
                .orders(orderFetchResponseModels)
                .build();
    }

    public UpdateOrderResponse update(UpdateOrderRequest request)
    {

        Order oldOrder = orderRepository.findById(request.getOrderId()).orElseThrow(()-> new EntityNotFoundException(Constants.ORDER_NOT_FOUND_CONST + request.getOrderId()));
        List<ItemQuantityModel> newItemQuantities = new ArrayList<>();

        //Delete old OrderItem relations
        List<OrderItem> oldRelations = orderItemRepository.findAll()
                .stream()
                .filter(e->e.getOrder().getId().equals(request.getOrderId()))
                .toList();
        for(OrderItem relation : oldRelations){
            orderItemRepository.deleteById(relation.getId());
        }

        //Update order last update date
        oldOrder.setLastUpdateDate(new Date());
        Order newOrder = orderRepository.save(oldOrder);
        //Create new relations
        for(var itemQuantityModel : request.getItemQuantityModels())
        {
            Item item = itemRepository.findById(itemQuantityModel.getItem().getId()).orElseThrow(
                    ()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + itemQuantityModel.getItem().getId()
                    )
            );

            newItemQuantities.add
                (
                    ItemQuantityModel
                            .builder()
                            .item(itemQuantityModel.getItem())
                            .quantity(itemQuantityModel
                            .getQuantity())
                            .build()
                );

            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .item(item)
                    .quantity(itemQuantityModel.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        return UpdateOrderResponse
                .builder()
                .updatedOrder(OrderUpdateModel
                    .builder()
                    .order_id(request.getOrderId())
                    .updatedOrderItems(newItemQuantities)
                    .build())
                .build();
    }

    public void delete(Long id){
        return;
    }

    public FindOrderResponse find(Long id){

        Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Constants.ORDER_NOT_FOUND_CONST + id));

            List<OrderItem> b = orderItemRepository.findAll().stream().filter(x->x.getOrder().getId().equals(order.getId())).toList();
            List<ItemQuantityModel> itemQuantityModels = new ArrayList<>();

            for(var orderItem : b){

                var itemQuantityModel = ItemQuantityModel.builder().item(orderItem.getItem()).quantity(orderItem.getQuantity()).build();
                itemQuantityModels.add(itemQuantityModel);

            }

        OrderFetchModel orderFetchResponseModels = OrderFetchModel
                .builder()
                .UserId(order.getUserId())
                .itemQuantityModels(itemQuantityModels)
                .OrderId(order.getId())
                .build();


        return FindOrderResponse.builder().orderFetchModel(orderFetchResponseModels).build();
    }
}
