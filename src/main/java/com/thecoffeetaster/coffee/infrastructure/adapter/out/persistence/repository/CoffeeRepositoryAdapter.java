package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.repository;

import com.thecoffeetaster.coffee.domain.exception.CoffeeException;
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
        Page<CoffeeEntity> coffees = repository.findAll(pageable);
        return coffees.map(CoffeeMapper::toDomain);
    }

    @Override
    public Coffee get(UUID id) {
        Optional<CoffeeEntity> coffeeEntity = repository.findById(id);
        if (coffeeEntity.isEmpty()) throw new CoffeeException(String.format("Coffee with id: %s not found", id));// TODO Custome Exception
        return CoffeeMapper.toDomain(coffeeEntity.get());
    }
}
