package com.example.response;

import com.example.model.MenuModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetMenuResponse {
    List<MenuModel> menuList;
}
