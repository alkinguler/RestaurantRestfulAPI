package com.example.model;

import com.example.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ItemQuantityModel {
    private Item item;
    private int quantity;
}
