package com.thecoffeetaster.coffee.domain.port.in;

import com.thecoffeetaster.coffee.domain.model.BeanType;

import java.util.UUID;

public interface BeanTypeUseCase {
    BeanType get(UUID id);
}
