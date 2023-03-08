package com.example.response;

import com.example.model.OrderModel;
import lombok.Builder;

@Builder
public class CreateOrderResponse {
    OrderModel createdOrder;
}
