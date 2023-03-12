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
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional()
    public CreateOrderResponse create(CreateOrderRequest request){
        try {
            //Get order info
            OrderModel orderModel = request.getOrder();
            List<ItemQuantityModel> itemQuantityModels = new ArrayList<>();

            //Calculate totalPrice
            int totalPrice = orderModel.getOrderItems()
                    .stream()
                    .mapToInt(
                            orderItem -> orderItem.getQuantity() * (itemRepository.findById(orderItem.getItemId())
                                    .orElseThrow(()->new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + orderItem.getItemId()))
                                    .getPrice())
                    )
                    .sum();

            //Create order
            Order order = Order.builder()
                    .userId(orderModel.getUserId())
                    .date(new Date())
                    .totalPrice(totalPrice)
                    .build();

            //Save order to db
            Order savedOrder = orderRepository.save(order);

            //Create and save order-item relations to db
            orderModel.getOrderItems().forEach(
                    orderItemModel -> {
                        Item item = itemRepository.findById(orderItemModel.getItemId()).orElseThrow();

                        OrderItem orderItem = OrderItem.builder()
                                .quantity(orderItemModel.getQuantity())
                                .order(savedOrder)
                                .item(item)
                                .build();

                        ItemQuantityModel itemQuantityModel = ItemQuantityModel.builder()
                                .quantity(orderItemModel.getQuantity())
                                .item(item)
                                .build();

                        itemQuantityModels.add(itemQuantityModel);


                        orderItemRepository.save(orderItem);
                    }
            );

            CreatedOrderModel createdOrder = CreatedOrderModel
                    .builder()
                    .orderId(savedOrder.getId())
                    .orderedItems(itemQuantityModels)
                    .userId(savedOrder.getUserId())
                    .totalPrice(totalPrice)
                    .build();

            return CreateOrderResponse
                    .builder()
                    .createdOrder(createdOrder)
                    .build();
        }

        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public GetOrderResponse get(){
        //Prepare required elements to fetch all orders
        List<Order> orders = orderRepository.findAll();
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
                            .orderId(order.getId())
                            .userId(order.getUserId())
                            .build());
        }

        return GetOrderResponse.builder().orders(orderFetchResponseModels).build();

    }
    @Transactional(readOnly = true)
    public FindOrderResponse find(Long id){
        try {
            //Find order and order items
            Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Constants.ORDER_NOT_FOUND_CONST + id));
            List<OrderItem> orderItems = orderItemRepository.findAll().stream().filter(x->x.getOrder().getId().equals(order.getId())).toList();
            List<ItemQuantityModel> itemQuantityModels = new ArrayList<>();

            //add item quantity models to orderItems
            for(var orderItem : orderItems){

                ItemQuantityModel itemQuantityModel = ItemQuantityModel.builder().item(orderItem.getItem()).quantity(orderItem.getQuantity()).build();
                itemQuantityModels.add(itemQuantityModel);

            }

            //Create fetched model to use it on response model
            OrderFetchModel orderFetchResponseModels = OrderFetchModel
                    .builder()
                    .userId(order.getUserId())
                    .itemQuantityModels(itemQuantityModels)
                    .orderId(order.getId())
                    .build();


            return FindOrderResponse.builder().orderFetchModel(orderFetchResponseModels).build();
        }

        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
    @Transactional
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
                Item item = itemRepository.findById(orderItemModel.getItemId()).orElseThrow(
                        ()-> new EntityNotFoundException(Constants.ITEM_NOT_FOUND_CONST + orderItemModel.getItemId()
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
                            .orderId(request.getOrderId())
                            .updatedOrderItems(newItemQuantities)
                            .userId(newOrder.getUserId())
                            .totalPrice(newTotalPrice)
                            .build()
                    )
                    .build();
        }

        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
    @Transactional
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
