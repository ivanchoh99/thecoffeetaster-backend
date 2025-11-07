package com.thecoffeetaster.coffee.domain.port.out;


import com.thecoffeetaster.coffee.domain.model.BeanType;

import java.util.Optional;
import java.util.UUID;

public interface BeanTypeRepository {
    BeanType findById(UUID id);
    BeanType save(BeanType beanType);
}
