package com.example.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateMenuRequest {
    private Long menuId;
    private String day;
    private List<Long> itemIdList;
}
