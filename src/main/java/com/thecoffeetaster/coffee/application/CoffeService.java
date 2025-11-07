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
    private final BeanTypeUseCase beanTypeService;
    private final ProcessingMethodUseCase processingMethodService;


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
    public Coffee put(Coffee putCoffee) {
        return repository.save(putCoffee);
    }


    @Override
    public Coffee patch(UUID id, CoffeePatch patchCoffee) {
        Coffee coffee = repository.get(id);


        if (patchCoffee.beanType() != null) {
            BeanType beanType = beanTypeService.get(UUID.fromString(patchCoffee.beanType()));
            coffee.setBeanType(beanType);
        }
        if (patchCoffee.processingMethod() != null) {
            ProcessingMethod processingMethod = processingMethodService.get(UUID.fromString(patchCoffee.processingMethod()));
            coffee.setProcessingMethod(processingMethod);
        }
        if (patchCoffee.originCountry() != null) {
            coffee.setOriginCountry(patchCoffee.originCountry());
        }
        if (patchCoffee.originRegion() != null) {
            coffee.setOriginRegion(patchCoffee.originRegion());
        }
        if (patchCoffee.originCity() != null) {
            coffee.setOriginCity(patchCoffee.originCity());
        }
        if (patchCoffee.altitude() != null) {
            coffee.setAltitude(patchCoffee.altitude());
        }
        if (patchCoffee.brand() != null) {
            coffee.setBrand(patchCoffee.brand());
        }
        if (patchCoffee.tastingNotes() != null) {
            coffee.setTastingNotes(patchCoffee.tastingNotes());
        }
        if (patchCoffee.roaster() != null) {
            coffee.setRoaster(patchCoffee.roaster());
        }
        if (patchCoffee.roastLevel() != null) {
            coffee.setRoastLevel(patchCoffee.roastLevel());
        }
        if (patchCoffee.price() != null) {
            coffee.setPrice(patchCoffee.price());
        }

        return repository.save(coffee);
    }
}
