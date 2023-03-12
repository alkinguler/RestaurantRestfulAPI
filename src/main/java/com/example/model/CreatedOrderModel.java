package com.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class CreatedOrderModel {
    private Long userId;

    private Long orderId;

    private List<ItemQuantityModel> orderedItems;

    private int totalPrice;

}
