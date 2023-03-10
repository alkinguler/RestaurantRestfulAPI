package com.example.request;

import com.example.model.ItemQuantityModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    Long orderId;
    List<ItemQuantityModel> itemQuantityModels;
}
