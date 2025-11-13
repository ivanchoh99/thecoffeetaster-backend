package com.thecoffeetaster.coffee.domain.port.in;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;

import java.util.List;
import java.util.UUID;

public interface ProcessingMethodUseCase {
    ProcessingMethod get(UUID id);
    List<ProcessingMethod> getAll();
    ProcessingMethod create(String name);
    ProcessingMethod update(ProcessingMethod processingMethod);
}
