package com.example.controller;

import com.example.request.CreateMenuRequest;
import com.example.request.UpdateMenuRequest;
import com.example.response.CreateMenuResponse;
import com.example.response.GetMenuResponse;
import com.example.response.ServiceResponseModel;
import com.example.response.UpdateMenuResponse;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ServiceResponseModel<GetMenuResponse>  get(){
//        var response = new ServiceResponseModel<GetMenuResponse>();
//        response.setResponseBody();
        var x = ServiceResponseModel.success(menuService.get());
        return x;
    }

    @GetMapping("/{day}")
    public ServiceResponseModel<GetMenuResponse> find(@PathVariable String day){
        return ServiceResponseModel.success(menuService.find(day));
    }

    @PostMapping()
    public ServiceResponseModel<CreateMenuResponse> create(@RequestBody CreateMenuRequest request){
        return null; // TODO
    }

    @PutMapping("/{id}")
    public ServiceResponseModel<UpdateMenuResponse> update(@RequestBody UpdateMenuRequest request){
        return null; // TODO
    }

    @DeleteMapping("/{id}")
    public ServiceResponseModel<?> delete(@PathVariable Long id){
        menuService.delete(id);
        return ServiceResponseModel.success();
    }

}
