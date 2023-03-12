package com.example.response;

import com.example.model.MenuModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetMenuResponse {
    private List<MenuModel> menuList;
}
