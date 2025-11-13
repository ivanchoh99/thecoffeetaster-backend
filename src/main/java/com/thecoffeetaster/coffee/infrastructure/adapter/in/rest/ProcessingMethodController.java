package com.thecoffeetaster.coffee.infrastructure.adapter.in.rest;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.port.in.ProcessingMethodUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/processing-method")
@RequiredArgsConstructor
public class ProcessingMethodController {

    private final ProcessingMethodUseCase service;

    @PostMapping
    public ResponseEntity<ProcessingMethod> post(@RequestParam String name) {
        ProcessingMethod processingMethod = service.create(name);
        return ResponseEntity.ok(processingMethod);
    }

    @GetMapping
    public ResponseEntity<List<ProcessingMethod>> getAll() {
        List<ProcessingMethod> processingMethod = service.getAll();
        return ResponseEntity.ok(processingMethod);
    }

    @GetMapping
    public ResponseEntity<ProcessingMethod> get(@RequestParam String id) {
        ProcessingMethod processingMethod = service.get(UUID.fromString(id));
        return ResponseEntity.ok(processingMethod);
    }

    @PutMapping
    public ResponseEntity<ProcessingMethod> put(@RequestBody ProcessingMethod processingMethodUpdate) {
        ProcessingMethod processingMethod = service.update(processingMethodUpdate);
        return ResponseEntity.ok(processingMethod);
    }

}
