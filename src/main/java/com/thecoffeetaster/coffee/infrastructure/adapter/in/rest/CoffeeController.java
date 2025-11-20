package com.thecoffeetaster.coffee.infrastructure.adapter.in.rest;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.port.in.BeanTypeUseCase;
import com.thecoffeetaster.coffee.domain.port.in.CoffeeUseCase;
import com.thecoffeetaster.coffee.domain.port.in.ProcessingMethodUseCase;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeePatch;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeePost;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeeUpdate;
import com.thecoffeetaster.coffee.infrastructure.adapter.mapper.CoffeeMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {

    private final CoffeeUseCase service;
    private final BeanTypeUseCase beanTypeService;
    private final ProcessingMethodUseCase processingMethodService;


    @PostMapping("/create")
    public ResponseEntity<Coffee> create(@Valid CoffeePost coffeePost) {
        BeanType beanType = beanTypeService.get(UUID.fromString(coffeePost.beanType()));
        ProcessingMethod processingMethod = processingMethodService.get(UUID.fromString(coffeePost.processingMethod()));
        Coffee coffee = CoffeeMapper.toDomain(coffeePost, beanType, processingMethod);
        return ResponseEntity.ok(service.create(coffee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getCoffee(@PathVariable String id) {
        Coffee coffee = service.get(UUID.fromString(id));
        return ResponseEntity.ok(coffee);
    }

    @GetMapping
    public ResponseEntity<Page<Coffee>> getCoffees(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int pageSize) {
        Page<Coffee> coffees = service.get(page, pageSize);
        return ResponseEntity.ok(coffees);
    }

    @PutMapping
    public ResponseEntity<Coffee> put(CoffeeUpdate coffeeUpdate) {
        BeanType beanType = beanTypeService.get(UUID.fromString(coffeeUpdate.beanType()));
        ProcessingMethod processingMethod = processingMethodService.get(UUID.fromString(coffeeUpdate.processingMethod()));
        Coffee coffee = CoffeeMapper.toDomain(coffeeUpdate, beanType, processingMethod);
        Coffee coffeeUpdated = service.update(coffee);
        return ResponseEntity.ok(coffeeUpdated);
    }

    @PatchMapping
    public ResponseEntity<Coffee> patch(
            @RequestParam String id,
            @RequestBody CoffeePatch coffeePatch) {

        BeanType beanType = null;
        ProcessingMethod processingMethod = null;

        if (coffeePatch.beanType() != null) beanType = beanTypeService.get(UUID.fromString(coffeePatch.beanType()));
        if (coffeePatch.beanType() != null) processingMethod = processingMethodService.get(UUID.fromString(coffeePatch.processingMethod()));

        Coffee coffeeToPatch = CoffeeMapper.toDomain(coffeePatch, beanType, processingMethod);
        Coffee coffee = service.patch(UUID.fromString(id), coffeeToPatch);
        return ResponseEntity.ok(coffee);
    }

}
