package com.thecoffeetaster.coffee.domain.port.out;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;

import java.util.Optional;
import java.util.UUID;

public interface ProcessingMethodRepository {
    ProcessingMethod findById(UUID id);
    ProcessingMethod save(ProcessingMethod processingMethod);
}
