package com.thecoffeetaster.coffee.domain.port.in;

import com.thecoffeetaster.coffee.domain.model.BeanType;

import java.util.List;
import java.util.UUID;

public interface BeanTypeUseCase {
    BeanType get(UUID id);
    List<BeanType> getAll();
    BeanType create(String name);
    BeanType update(BeanType beanType);
}
