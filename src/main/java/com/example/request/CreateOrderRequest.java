package com.example.request;

import com.example.model.OrderModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private OrderModel order;
}
