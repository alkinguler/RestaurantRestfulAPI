package com.example.response;

import com.example.model.OrderFetchModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class GetOrderResponse {
    private List<OrderFetchModel> orders;
}
