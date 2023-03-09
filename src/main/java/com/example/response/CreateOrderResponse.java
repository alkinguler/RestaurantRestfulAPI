package com.example.response;

import com.example.model.OrderModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateOrderResponse {
    OrderModel createdOrder;
}
