package com.example.request;

import com.example.model.OrderItemModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class UpdateOrderRequest {
    Long orderId;
    List<OrderItemModel> orderItemModels;
}
