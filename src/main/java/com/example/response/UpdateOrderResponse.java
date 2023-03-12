package com.example.response;


import com.example.model.OrderUpdateModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateOrderResponse {
    private OrderUpdateModel updatedOrder;
}
