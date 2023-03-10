package com.example.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OrderFetchModel {
    private Long orderId;
    private Long userId;
    private List<ItemQuantityModel> itemQuantityModels;
}
