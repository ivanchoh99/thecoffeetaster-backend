package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.repository;

import com.thecoffeetaster.coffee.domain.exception.BeanTypeException;
import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.port.out.BeanTypeRepository;
import com.thecoffeetaster.coffee.infrastructure.adapter.mapper.BeanTypeMapper;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.BeanTypeEntity;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa.BeanTypeJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class BeanTypeRespositoryAdapter implements BeanTypeRepository {

    private final BeanTypeJpaRepository repository;

    @Override
    public BeanType findById(UUID id) {
        Optional<BeanTypeEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new BeanTypeException(String.format("BeanType with id: %s not found", id));//TODO Custome Exception
        }
        return BeanTypeMapper.toDomain(entity.get());
    }

    @Override
    public List<BeanType> getAll() {
        List<BeanTypeEntity> entities = repository.findAll();
        return entities.stream().map(BeanTypeMapper::toDomain).toList();
    }

    @Override
    public BeanType save(BeanType beanType) {
        BeanTypeEntity entity = BeanTypeMapper.toEntity(beanType);
        BeanTypeEntity beanTypeSaved = repository.save(entity);
        return BeanTypeMapper.toDomain(beanTypeSaved);
    }
}
