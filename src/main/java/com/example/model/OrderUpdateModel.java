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
    List<Item> updatedOrderItems;
}
