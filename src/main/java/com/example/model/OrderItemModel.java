package com.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemModel {
    private Long itemId;
    private int quantity;
}
