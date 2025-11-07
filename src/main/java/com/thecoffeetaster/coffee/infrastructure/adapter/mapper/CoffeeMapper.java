package com.thecoffeetaster.coffee.infrastructure.adapter.mapper;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.domain.model.RoastLevel;
import com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request.CoffeePost;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.BeanTypeEntity;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.CoffeeEntity;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.ProcessingMethodEntity;

public class CoffeeMapper {

    private CoffeeMapper() {
        throw  new IllegalStateException("Mapper Class");
    }

    public static Coffee toDomain(CoffeePost coffeePost, BeanType beanType, ProcessingMethod processingMethod, RoastLevel roastLevel){
        return Coffee.builder(coffeePost.brand(), coffeePost.price())
                .altitude(coffeePost.altitude())
                .beanType(beanType)
                .processingMethod(processingMethod)
                .originCountry(coffeePost.originCountry())
                .originRegion(coffeePost.originRegion())
                .originCity(coffeePost.originCity())
                .roaster(coffeePost.roaster())
                .tastingNotes(coffeePost.tastingNotes())
                .roastLevel(roastLevel)
                .build();
    }

    public static Coffee toDomain(CoffeeEntity coffeeEntity){
        BeanType beanType = BeanTypeMapper.toDomain(coffeeEntity.getBeanType());
        ProcessingMethod processingMethod = ProcessingMethodMapper.toDomain(coffeeEntity.getProcessingMethod());
        RoastLevel roastLevel = RoastLevel.fromString(coffeeEntity.getRoastLevel());
        return Coffee.builder(coffeeEntity.getBrand(), coffeeEntity.getPrice())
                .altitude(coffeeEntity.getAltitude())
                .beanType(beanType)
                .processingMethod(processingMethod)
                .originCountry(coffeeEntity.getOriginCountry())
                .originRegion(coffeeEntity.getOriginRegion())
                .originCity(coffeeEntity.getOriginCity())
                .roaster(coffeeEntity.getRoaster())
                .tastingNotes(coffeeEntity.getTastingNotes())
                .roastLevel(roastLevel)
                .build();
    }

    public static CoffeeEntity toEntity(Coffee coffee) {
        BeanTypeEntity beanTypeEntity = BeanTypeMapper.toEntity(coffee.getBeanType());
        ProcessingMethodEntity processingMethodEntity = ProcessingMethodMapper.toEnity(coffee.getProcessingMethod());
        return CoffeeEntity.builder()
                .id(coffee.getId())
                .brand(coffee.getBrand())
                .originCountry(coffee.getOriginCountry())
                .originRegion(coffee.getOriginRegion())
                .originCity(coffee.getOriginCity())
                .beanType(beanTypeEntity)
                .processingMethod(processingMethodEntity)
                .createdAt(coffee.getCreatedAt())
                .updatedAt(coffee.getUpdatedAt())
                .roaster(coffee.getRoaster())
                .altitude(coffee.getAltitude())
                .tastingNotes(coffee.getTastingNotes())
                .build();
    }
}
