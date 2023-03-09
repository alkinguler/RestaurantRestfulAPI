package com.example.response;

import com.example.model.MenuModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Builder
@Setter
@Getter
public class CreateMenuResponse {
    MenuModel createdMenu;
}
