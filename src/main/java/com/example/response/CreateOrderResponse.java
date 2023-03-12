package com.example.response;

import com.example.model.CreatedOrderModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateOrderResponse {
    private CreatedOrderModel createdOrder;
}
