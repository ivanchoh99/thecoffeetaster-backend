package com.thecoffeetaster.coffee.application;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.port.in.BeanTypeUseCase;
import com.thecoffeetaster.coffee.domain.port.in.CoffeeUseCase;
import com.thecoffeetaster.coffee.domain.port.in.ProcessingMethodUseCase;
import com.thecoffeetaster.coffee.domain.port.out.CoffeeRepository;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeePatch;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CoffeService implements CoffeeUseCase {

    private final CoffeeRepository repository;


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
        return repository.get(id);
    }

    @Override
    public Coffee update(Coffee putCoffee) {
        putCoffee.setUpdatedAt(LocalDateTime.now());
        return repository.save(putCoffee);
    }


    @Override
    public Coffee patch(UUID id, Coffee patchCoffee) {
        Coffee coffee = repository.get(id);

        if (patchCoffee.getBeanType() != null) {
            coffee.setBeanType(patchCoffee.getBeanType());
        }
        if (patchCoffee.getProcessingMethod() != null) {
            coffee.setProcessingMethod(patchCoffee.getProcessingMethod());
        }
        if (patchCoffee.getOriginCountry() != null) {
            coffee.setOriginCountry(patchCoffee.getOriginCountry());
        }
        if (patchCoffee.getOriginRegion() != null) {
            coffee.setOriginRegion(patchCoffee.getOriginRegion());
        }
        if (patchCoffee.getOriginCity() != null) {
            coffee.setOriginCity(patchCoffee.getOriginCity());
        }
        if (patchCoffee.getAltitude() != null) {
            coffee.setAltitude(patchCoffee.getAltitude());
        }
        if (patchCoffee.getBrand() != null) {
            coffee.setBrand(patchCoffee.getBrand());
        }
        if (patchCoffee.getTastingNotes() != null) {
            coffee.setTastingNotes(patchCoffee.getTastingNotes());
        }
        if (patchCoffee.getRoaster() != null) {
            coffee.setRoaster(patchCoffee.getRoaster());
        }
        if (patchCoffee.getRoastLevel() != null) {
            coffee.setRoastLevel(patchCoffee.getRoastLevel());
        }
        if (patchCoffee.getPrice() != null) {
            coffee.setPrice(patchCoffee.getPrice());
        }

        coffee.setUpdatedAt(LocalDateTime.now());
        return repository.save(coffee);
    }
}
