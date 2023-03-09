package com.example.request;

import com.example.model.MenuModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class CreateMenuRequest {
    String day;

    List<Long> itemIdList;
}
