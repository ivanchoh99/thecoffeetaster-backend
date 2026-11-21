package com.thecoffeetaster.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/coffees")
class CoffeeController {

    @PostMapping
    fun createCoffee(): ResponseEntity<Void> = ResponseEntity.noContent().build()

    @GetMapping("/{id}")
    fun getCoffee(@PathVariable id: String): ResponseEntity<Void> = ResponseEntity.noContent().build()

    @GetMapping
    fun getCoffees(): ResponseEntity<Void> = ResponseEntity.noContent().build()

    @PatchMapping("/{id}")
    fun patchCoffee(@PathVariable id: String): ResponseEntity<Void> = ResponseEntity.noContent().build()

    @PutMapping("/{id}")
    fun putCoffee(@PathVariable id: String): ResponseEntity<Void> = ResponseEntity.noContent().build()
}
