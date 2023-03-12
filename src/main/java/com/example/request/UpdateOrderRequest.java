package com.example.request;

import com.example.model.OrderItemModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    private Long orderId;
    private List<OrderItemModel> orderItemModels;
}
