package com.example.model;

import com.example.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MenuModel {
    private Long menuId;
    private String day;
    private List<Item> menuItems;
}
