package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.repository;

import com.thecoffeetaster.coffee.domain.port.out.CoffeeRepository;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.infrastructure.adapter.mapper.CoffeeMapper;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.CoffeeEntity;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa.CoffeeJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class CoffeeRepositoryAdapter implements CoffeeRepository {

    private final CoffeeJpaRepository repository;

    @Override
    public Coffee save(Coffee coffee) {
        CoffeeEntity coffeeEntity = CoffeeMapper.toEntity(coffee);
        CoffeeEntity coffeeSaved = repository.save(coffeeEntity);
        return CoffeeMapper.toDomain(coffeeSaved);
    }

    @Override
    public Page<Coffee> get(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Coffee> get(UUID id) {
        return repository.findById(id);
    }
}
