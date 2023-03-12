package com.example.response;

import com.example.model.MenuModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateMenuResponse {
    MenuModel updatedMenu;
}
