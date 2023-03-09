package com.example.controller;

import com.example.model.ErrorModel;
import com.example.request.CreateMenuRequest;
import com.example.request.UpdateMenuRequest;
import com.example.response.CreateMenuResponse;
import com.example.response.GetMenuResponse;
import com.example.response.ServiceResponseModel;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ServiceResponseModel<GetMenuResponse>  get()
    {
        return ServiceResponseModel.success(menuService.get());
    }

    @GetMapping("/{day}")
    public ServiceResponseModel<GetMenuResponse> find(@PathVariable String day){
        return ServiceResponseModel.success(menuService.find(day));
    }

    @PostMapping()
    public ServiceResponseModel<CreateMenuResponse> create(@RequestBody CreateMenuRequest request){
        return ServiceResponseModel.success(menuService.create(request));
    }

    @PutMapping()
    public ResponseEntity<ServiceResponseModel<?>> update(@RequestBody UpdateMenuRequest request){
        try
        {
            return new ResponseEntity<>(ServiceResponseModel.success(menuService.update(request)),HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(
                    ServiceResponseModel.failure(e.getMessage()),
                    e.getStatusCode());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponseModel<?>> delete(@PathVariable Long id){
        try
        {
            menuService.delete(id);
            return new ResponseEntity<>(ServiceResponseModel.success(),HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(
                    ServiceResponseModel.failure(e.getMessage()),
                    e.getStatusCode());
        }
    }


}
