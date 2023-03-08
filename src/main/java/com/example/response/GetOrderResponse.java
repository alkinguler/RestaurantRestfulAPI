package com.example.response;

import com.example.model.OrderModel;
import lombok.Builder;

import java.util.List;
@Builder
public class GetOrderResponse {
    List<OrderModel> orders;
}
