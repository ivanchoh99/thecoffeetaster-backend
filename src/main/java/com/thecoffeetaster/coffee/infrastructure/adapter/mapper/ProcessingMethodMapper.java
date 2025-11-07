package com.thecoffeetaster.coffee.infrastructure.adapter.mapper;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.ProcessingMethodEntity;

public class ProcessingMethodMapper {

    public static ProcessingMethodEntity toEnity(ProcessingMethod processingMethod){
        return ProcessingMethodEntity.builder()
                .id(processingMethod.getId())
                .name(processingMethod.getName())
                .build();
    }

    public static ProcessingMethod toDomain(ProcessingMethodEntity processingMethod) {
        return ProcessingMethod.builder()
                .id(processingMethod.getId())
                .name(processingMethod.getName())
                .build();
    }
}
