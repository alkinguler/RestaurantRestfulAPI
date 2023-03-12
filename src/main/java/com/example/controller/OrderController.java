package com.example.controller;

import com.example.request.CreateOrderRequest;
import com.example.request.UpdateOrderRequest;
import com.example.response.*;
import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ServiceResponseModel<?>> create(@RequestBody CreateOrderRequest request){
        try {
            return new ResponseEntity<>(ServiceResponseModel.success(orderService.create(request)),HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }
    }
    @GetMapping
    public ResponseEntity<ServiceResponseModel<?>> get(){
        try {
            return new ResponseEntity<>(ServiceResponseModel.success(orderService.get()),HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseModel<?>> find(@PathVariable Long id){
        try {
            return new ResponseEntity<>(ServiceResponseModel.success(orderService.find(id)),HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponseModel<?>> delete(@PathVariable Long id){

        try {
            orderService.delete(id);
            return new ResponseEntity<>(ServiceResponseModel.empty(),HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }
    }

    @PutMapping("")
    public ResponseEntity<ServiceResponseModel<?>> update(@RequestBody UpdateOrderRequest request){
        try {
            return new ResponseEntity<>(ServiceResponseModel.success(orderService.update(request)),HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }
    }

}
