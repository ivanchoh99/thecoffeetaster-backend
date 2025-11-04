package com.thecoffeetaster.coffee.infrastructure.adapter.in.rest;

import com.thecoffeetaster.coffee.application.port.in.CoffeeUseCase;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import org.springframework.web.bind.annotation.*;

@RestController
public class CoffeeController {

    private final CoffeeUseCase coffeeService;

    public CoffeeController(CoffeeUseCase coffeeService) {
        this.coffeeService = coffeeService;
    }

    @PostMapping
    public void create(Coffee coffee){

    }

    @GetMapping
    public void getCoffee(String id){
    }

    @GetMapping
    public void getCoffees(){

    }

    @PutMapping
    public void put(){

    }

    @PatchMapping
    public void patch(){

    }

}
