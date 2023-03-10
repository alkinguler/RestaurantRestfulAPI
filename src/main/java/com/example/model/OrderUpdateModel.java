package com.example.model;

import com.example.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OrderUpdateModel {
    private Long order_id;
    private Long user_id;
    private List<ItemQuantityModel> updatedOrderItems;
}
