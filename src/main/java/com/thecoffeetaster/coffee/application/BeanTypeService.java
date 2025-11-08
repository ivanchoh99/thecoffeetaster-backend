package com.thecoffeetaster.coffee.application;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.port.in.BeanTypeUseCase;
import com.thecoffeetaster.coffee.domain.port.out.BeanTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BeanTypeService implements BeanTypeUseCase {

    private final BeanTypeRepository repository;

    @Override
    public BeanType get(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<BeanType> getAll() {
        return repository.getAll();
    }

    @Override
    public BeanType create(String name) {
        BeanType newBeanType = BeanType.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
        return repository.save(newBeanType);
    }

    @Override
    public BeanType update(BeanType beanType) {
        return repository.save(beanType);
    }
}
