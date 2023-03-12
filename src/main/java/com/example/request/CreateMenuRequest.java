package com.example.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateMenuRequest {
    private String day;

    private List<Long> itemIdList;
}
