package com.thecoffeetaster.coffee.domain.port.out;


import com.thecoffeetaster.coffee.domain.model.BeanType;

import java.util.List;
import java.util.UUID;

public interface BeanTypeRepository {
    BeanType findById(UUID id);
    List<BeanType> getAll();
    BeanType save(BeanType beanType);
}
