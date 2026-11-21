package com.thecoffeetaster.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CoffeeController {

    @PostMapping
    fun createCoffee() {}

    @GetMapping
    fun getCoffee(id: String) {}

    @GetMapping
    fun getCoffees() {}

    @PatchMapping
    fun patchUser() {}

    @PutMapping
    fun putUser() {}
}
