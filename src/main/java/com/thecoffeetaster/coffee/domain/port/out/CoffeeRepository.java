package com.thecoffeetaster.coffee.domain.port.out;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CoffeeRepository {
    Coffee save(Coffee coffee);
    Page<Coffee> get(Pageable pageable);
    Coffee get(UUID id);
}
