package com.thecoffeetaster.coffee.application.service;

import com.thecoffeetaster.coffee.application.port.in.CoffeeUseCase;
import com.thecoffeetaster.coffee.application.port.out.CoffeeRepository;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import org.springframework.data.domain.Page;

import java.util.UUID;

public class CoffeService implements CoffeeUseCase {

    private final CoffeeRepository repository;

    public CoffeService(CoffeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Coffee create(Coffee newCoffee) {
        UUID id = UUID.randomUUID();
        newCoffee.setId(id);
        return repository.save(newCoffee);
    }

    @Override
    public Page<Coffee> get(int page, int pageSize) {
        return repository.get(page, pageSize);
    }

    @Override
    public Coffee get(UUID id) {
        return repository.get(id);
    }

    @Override
    public Coffee put(UUID id, Coffee putCoffee) {
        return repository.save(id, putCoffee);
    }

    @Override
    public Coffee patch(UUID id, Coffee patchCoffee) {
        Coffee coffee = repository.get(id);
        if (patchCoffee.getAltitude() != null) coffee.setAltitude(patchCoffee.getAltitude());
        if (patchCoffee.getOrigin() != null) coffee.setOrigin(patchCoffee.getOrigin());
        if (patchCoffee.getBrand() != null) coffee.setBrand(patchCoffee.getBrand());
        if (patchCoffee.getBeanType() != null) coffee.setBeanType(patchCoffee.getBeanType());
        if (patchCoffee.getProcess() != null) coffee.setProcess(patchCoffee.getProcess());
        if (patchCoffee.getTastingNotes() != null) coffee.setTastingNotes(patchCoffee.getTastingNotes());
        if (patchCoffee.getRoaster() != null) coffee.setRoaster(patchCoffee.getRoaster());
        if (patchCoffee.getRoastLevel() != null) coffee.setRoastLevel(patchCoffee.getRoastLevel());
        if (patchCoffee.getPrice() != null) coffee.setPrice(patchCoffee.getPrice());
        if (patchCoffee.getDate() != null) coffee.setDate(patchCoffee.getDate());

        return repository.save(id, coffee);
    }
}
