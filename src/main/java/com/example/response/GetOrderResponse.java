package com.example.response;

import com.example.model.OrderModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class GetOrderResponse {
    List<OrderModel> orders;
}
