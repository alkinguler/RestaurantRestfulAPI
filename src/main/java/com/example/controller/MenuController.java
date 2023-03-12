package com.example.controller;

import com.example.request.CreateMenuRequest;
import com.example.request.UpdateMenuRequest;
import com.example.response.ServiceResponseModel;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ServiceResponseModel<?>> get() {
        try {
            return new ResponseEntity<>(ServiceResponseModel.success(menuService.get()),HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{day}")
    public ResponseEntity<ServiceResponseModel<?>> find(@PathVariable String day){
        try
        {
            return new ResponseEntity<>(ServiceResponseModel.success(menuService.find(day)), HttpStatus.OK
            );
        }

        catch (ResponseStatusException e)
        {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }

    }

    @PostMapping()
    public ResponseEntity<ServiceResponseModel<?>> create(@RequestBody CreateMenuRequest request){
        try
        {
            return new ResponseEntity<>(ServiceResponseModel.success(menuService.create(request)),HttpStatus.OK);
        }

        catch (ResponseStatusException e) {
            return new ResponseEntity<>(ServiceResponseModel.failure(e.getMessage()), e.getStatusCode());
        }
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
