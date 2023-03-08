package com.example.request;

import com.example.model.OrderModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    OrderModel order;
}
