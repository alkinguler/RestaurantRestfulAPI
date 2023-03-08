package com.example.response;

import com.example.model.MenuModel;
import lombok.Builder;

@Builder
public class CreateMenuResponse {
    MenuModel createdMenu;
}
