package com.example.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class UpdateMenuRequest {
    private Long MenuId;
    private String Day;
    private List<Long> itemIdList;
}
