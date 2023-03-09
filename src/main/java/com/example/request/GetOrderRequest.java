package com.example.request;

import com.example.model.OrderModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetOrderRequest {
    OrderModel orderModel;
}