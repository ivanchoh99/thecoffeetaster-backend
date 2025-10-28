package com.thecoffeetaster.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CoffeeController {

    @PostMapping
    public void createCoffee(){
    }

    @GetMapping
    public void getCoffee(String id){
    }

    @GetMapping
    public void getCoffees(){

    }

    @PatchMapping
    public void patchUser(){

    }

    @PutMapping
    public void putUser(){

    }

}
