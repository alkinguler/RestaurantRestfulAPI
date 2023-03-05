package com.example.service;

import com.example.dao.MenuRepository;
import com.example.model.Menu;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final MenuRepository menuRepository;


}
