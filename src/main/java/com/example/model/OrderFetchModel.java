package com.example.model;


import com.example.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OrderFetchModel {
    private Long OrderId;
    private Long UserId;
    private List<ItemQuantityModel> itemQuantityModels;
}
