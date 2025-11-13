package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.repository;

import com.thecoffeetaster.coffee.domain.exception.BeanTypeException;
import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.port.out.ProcessingMethodRepository;
import com.thecoffeetaster.coffee.infrastructure.adapter.mapper.ProcessingMethodMapper;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.ProcessingMethodEntity;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa.ProcessingMethodJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProcessingMethodAdapter implements ProcessingMethodRepository {

    private final ProcessingMethodJpaRepository repository;

    @Override
    public ProcessingMethod findById(UUID id) {
        Optional<ProcessingMethodEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new BeanTypeException(String.format("BeanType with id: %s not found", id));//TODO Custome Exception
        }
        return ProcessingMethodMapper.toDomain(entity.get());
    }

    @Override
    public List<ProcessingMethod> getAll() {
        List<ProcessingMethodEntity> entities = repository.findAll();
        return entities.stream().map(ProcessingMethodMapper::toDomain).toList();
    }

    @Override
    public ProcessingMethod save(ProcessingMethod processingMethod) {
        ProcessingMethodEntity entity = ProcessingMethodMapper.toEntity(processingMethod);
        ProcessingMethodEntity processingMethodSaved = repository.save(entity);
        return ProcessingMethodMapper.toDomain(processingMethodSaved);
    }
}
