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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final OrderItemRepository orderItemRepository;
    @Autowired
    private final ItemRepository itemRepository;

    public CreateOrderResponse create(CreateOrderRequest request){
        try {
            //Get order info
            OrderModel orderModel = request.getOrder();

            //Calculate totalPrice
            int TotalPrice = orderModel.getOrderItems()
                    .stream()
                    .mapToInt(
                            orderItem -> orderItem.getQuantity() * (itemRepository.findById(orderItem.getItem_id())
                                    .orElseThrow(()->new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + orderItem.getItem_id()))
                                    .getPrice())
                    )
                    .sum();

            //Create order
            Order order = Order.builder()
                    .userId(orderModel.getUserId())
                    .date(new Date())
                    .TotalPrice(TotalPrice)
                    .build();

            //Save order to db
            Order savedOrder = orderRepository.save(order);

            //Create and save order-item relations to db
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

        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public GetOrderResponse get(){
        //Prepare required elements to fetch all orders
        var orders = orderRepository.findAll();
        List<OrderFetchModel> orderFetchResponseModels = new ArrayList<>();

        //Process response model list
        for (var order : orders) {
            List<ItemQuantityModel> itemQuantityModels = orderItemRepository.findAll()
                    .stream()
                    .filter(x -> x.getOrder().getId().equals(order.getId()))
                    .map(orderItem -> ItemQuantityModel.builder()
                            .item(orderItem.getItem())
                            .quantity(orderItem.getQuantity())
                            .build())
                    .toList();

            orderFetchResponseModels.add(
                    OrderFetchModel.builder()
                            .itemQuantityModels(itemQuantityModels)
                            .OrderId(order.getId())
                            .UserId(order.getUserId())
                            .build());
        }

        return GetOrderResponse.builder().orders(orderFetchResponseModels).build();

    }

    public FindOrderResponse find(Long id){
        try {
            //Find order and order items
            Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Constants.ORDER_NOT_FOUND_CONST + id));
            List<OrderItem> orderItems = orderItemRepository.findAll().stream().filter(x->x.getOrder().getId().equals(order.getId())).toList();
            List<ItemQuantityModel> itemQuantityModels = new ArrayList<>();

            //add item quantity models to orderItems
            for(var orderItem : orderItems){

                var itemQuantityModel = ItemQuantityModel.builder().item(orderItem.getItem()).quantity(orderItem.getQuantity()).build();
                itemQuantityModels.add(itemQuantityModel);

            }

            //Create fetched model to use it on response model
            OrderFetchModel orderFetchResponseModels = OrderFetchModel
                    .builder()
                    .UserId(order.getUserId())
                    .itemQuantityModels(itemQuantityModels)
                    .OrderId(order.getId())
                    .build();


            return FindOrderResponse.builder().orderFetchModel(orderFetchResponseModels).build();
        }

        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public UpdateOrderResponse update(UpdateOrderRequest request)
    {
        try {
            //Prepare required elements to update
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
            for(var orderItemModel : request.getOrderItemModels())
            {
                Item item = itemRepository.findById(orderItemModel.getItem_id()).orElseThrow(
                        ()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + orderItemModel.getItem_id()
                        )
                );

                newItemQuantities.add
                        (
                                ItemQuantityModel
                                        .builder()
                                        .quantity(orderItemModel.getQuantity())
                                        .item(item)
                                        .build()
                        );


                OrderItem orderItem = OrderItem.builder()
                        .order(oldOrder)
                        .item(item)
                        .quantity(orderItemModel.getQuantity())
                        .build();

                orderItemRepository.save(orderItem);
            }

            //Calculate totalPrice with new items
            int newTotalPrice = orderItemRepository.findAll().stream().filter(x->x.getOrder().getId().equals(newOrder.getId()))
                    .mapToInt(
                            orderItem -> orderItem.getQuantity() * (itemRepository.findById(orderItem.getItem().getId())
                                    .orElseThrow(()->new RuntimeException(Constants.ITEM_NOT_FOUND_CONST + orderItem.getItem().getId()))
                                    .getPrice())
                    )
                    .sum();


            //Set total price with new items
            newOrder.setTotalPrice(newTotalPrice);
            orderRepository.save(newOrder);


            return UpdateOrderResponse
                    .builder()
                    .updatedOrder(OrderUpdateModel
                            .builder()
                            .order_id(request.getOrderId())
                            .updatedOrderItems(newItemQuantities)
                            .user_id(newOrder.getUserId())
                            .build())
                    .build();
        }

        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public void delete(Long id){
        try {
            //Validation of id
            orderRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(Constants.ORDER_NOT_FOUND_CONST + id));

            //Delete order-item relations
            List<Long> orderItemIds = orderItemRepository
                    .findAll()
                    .stream()
                    .filter(orderItem -> orderItem.getOrder().getId().equals(id))
                    .map(OrderItem::getId)
                    .toList();
            for (Long orderItemId :orderItemIds){
                orderItemRepository.deleteById(orderItemId);
            }

            //Delete order
            orderRepository.deleteById(id);
        }

        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }


}
