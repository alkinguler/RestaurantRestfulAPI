package com.example.request;

import com.example.model.OrderModel;
import lombok.Builder;

@Builder
public class GetOrderRequest {
    OrderModel orderModel;
}