package com.example.response;

import com.example.model.MenuModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class CreateMenuResponse {
    private MenuModel createdMenu;
}
