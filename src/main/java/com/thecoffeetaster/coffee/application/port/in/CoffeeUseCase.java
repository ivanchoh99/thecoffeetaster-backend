package com.thecoffeetaster.coffee.application.port.in;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CoffeeUseCase {
    Coffee create(Coffee coffee);
    Page<Coffee> get(int page, int pageSize);
    Coffee get(UUID id);
    Coffee put(UUID id, Coffee coffee);
    Coffee patch(UUID id, Coffee coffee);
}
