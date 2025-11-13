package com.thecoffeetaster.coffee.infrastructure.adapter.in.rest;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.port.in.BeanTypeUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/beanType")
public class BeanTypeController {

    private final BeanTypeUseCase service;

    @PostMapping
    public ResponseEntity<BeanType> post(@RequestParam String name) {
        BeanType beanType = service.create(name);
        return ResponseEntity.ok(beanType);
    }

    @GetMapping
    public ResponseEntity<List<BeanType>> getAll() {
        List<BeanType> beanTypes = service.getAll();
        return ResponseEntity.ok(beanTypes);
    }

    @GetMapping
    public ResponseEntity<BeanType> get(@RequestParam String id) {
        BeanType beanType = service.get(UUID.fromString(id));
        return ResponseEntity.ok(beanType);
    }

    @PutMapping
    public ResponseEntity<BeanType> put(@RequestBody BeanType beanTypeUpdate) {
        BeanType beanType = service.update(beanTypeUpdate);
        return ResponseEntity.ok(beanType);
    }
}
