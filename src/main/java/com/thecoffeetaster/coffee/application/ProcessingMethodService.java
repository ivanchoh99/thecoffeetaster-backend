package com.thecoffeetaster.coffee.application;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.port.in.ProcessingMethodUseCase;
import com.thecoffeetaster.coffee.domain.port.out.ProcessingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessingMethodService  implements ProcessingMethodUseCase {

    private final ProcessingMethodRepository repository;

    @Override
    public ProcessingMethod get(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<ProcessingMethod> getAll() {
        return repository.getAll();
    }

    @Override
    public ProcessingMethod create(String name) {
        ProcessingMethod newProcessingMethod = ProcessingMethod.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
        return repository.save(newProcessingMethod);
    }

    @Override
    public ProcessingMethod update(ProcessingMethod processingMethod) {
        return repository.save(processingMethod);
    }
}
