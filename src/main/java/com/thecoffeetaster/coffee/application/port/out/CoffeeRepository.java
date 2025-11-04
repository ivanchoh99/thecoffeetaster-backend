package com.thecoffeetaster.coffee.application.port.out;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CoffeeRepository {

    Coffee save(UUID id, Coffee coffee);
    Coffee save(Coffee coffee);
    Page<Coffee> get(int page, int pageSize);
    Coffee get(UUID id);
    Coffee update(UUID id, Coffee coffee);
    boolean exist(UUID id);
}
