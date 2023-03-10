package com.example.controller;

import com.example.request.CreateOrderRequest;
import com.example.request.UpdateOrderRequest;
import com.example.response.*;
import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ServiceResponseModel<CreateOrderResponse> create(@RequestBody CreateOrderRequest request){
        var x = ServiceResponseModel.success(orderService.create(request));
        return x;
    }
    @GetMapping
    public ServiceResponseModel<GetOrderResponse> get(){
        return ServiceResponseModel.success(orderService.get());
    }

    @GetMapping("/{id}")
    public ServiceResponseModel<FindOrderResponse> find(@PathVariable Long id){
        return ServiceResponseModel.success(orderService.find(id));
    }
    @DeleteMapping("/{id}")
    public ServiceResponseModel<?> delete(@PathVariable Long id){
        orderService.delete(id);
        return ServiceResponseModel.empty();
    }

    @PutMapping("")
    public ServiceResponseModel<UpdateOrderResponse> update(@RequestBody UpdateOrderRequest request){
        return ServiceResponseModel.success(orderService.update(request));
    }

}
