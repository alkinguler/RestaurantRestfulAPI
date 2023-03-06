package com.example.controller;

import com.example.dao.OrderRepository;
import com.example.model.Order;
import com.example.model.OrderDto;
import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public Order createOrder(@RequestBody OrderDto orderDto){
        return orderService.createOrder(orderDto);
    }

}
