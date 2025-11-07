package com.thecoffeetaster.coffee.domain.port.in;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeePatch;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CoffeeUseCase {
    Coffee create(Coffee coffee);
    Page<Coffee> get(int page, int pageSize);
    Coffee get(UUID id);
    Coffee put(Coffee coffee);
    Coffee patch(UUID id, Coffee coffee);
}
