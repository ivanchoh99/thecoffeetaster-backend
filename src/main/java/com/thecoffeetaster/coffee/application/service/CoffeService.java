package com.thecoffeetaster.coffee.application.service;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.domain.port.in.CoffeeUseCase;
import com.thecoffeetaster.coffee.domain.port.out.CoffeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CoffeService implements CoffeeUseCase {

    private final CoffeeRepository repository;

    public CoffeService(CoffeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Coffee create(Coffee newCoffee) {
        UUID id = UUID.randomUUID();
        newCoffee.setId(id);
        newCoffee.setCreatedAt(LocalDateTime.now());
        newCoffee.setUpdatedAt(LocalDateTime.now());
        return repository.save(newCoffee);
    }

    @Override
    public Page<Coffee> get(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return repository.get(pageable);
    }

    @Override
    public Coffee get(UUID id) {
        Optional<Coffee> coffee = repository.get(id);
        return coffee.orElseThrow();
    }

    @Override
    public Coffee put(Coffee putCoffee) {
        return repository.save(putCoffee);
    }

    @Override
    public Coffee patch(UUID id, Coffee patchCoffee) {
       return null;
    }
}
