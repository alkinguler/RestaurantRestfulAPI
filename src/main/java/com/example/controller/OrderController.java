package com.example.controller;

import com.example.request.CreateOrderRequest;
import com.example.request.UpdateOrderRequest;
import com.example.response.CreateOrderResponse;
import com.example.response.GetOrderResponse;
import com.example.response.ServiceResponseModel;
import com.example.response.UpdateOrderResponse;
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
        return ServiceResponseModel.success(orderService.create(request));
    }
    @GetMapping
    public ServiceResponseModel<GetOrderResponse> get(){
        return ServiceResponseModel.success(orderService.get());
    }
    @DeleteMapping("/{id}")
    public ServiceResponseModel<?> delete(@PathVariable Long id){
        orderService.delete(id);
        return ServiceResponseModel.empty();
    }

    @PutMapping("/{id}")
    public ServiceResponseModel<UpdateOrderResponse> update(@RequestBody UpdateOrderRequest request){
        return ServiceResponseModel.success(orderService.update(request));
    }

}
