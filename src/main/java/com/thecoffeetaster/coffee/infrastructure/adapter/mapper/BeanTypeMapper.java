package com.thecoffeetaster.coffee.infrastructure.adapter.mapper;

import com.thecoffeetaster.coffee.domain.model.BeanType;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.BeanTypeEntity;

public final class BeanTypeMapper {

    private BeanTypeMapper(){
        throw new IllegalStateException("Mapper Class");
    }

    public static BeanTypeEntity toEntity(BeanType beanType){
        return BeanTypeEntity.builder()
                .id(beanType.getId())
                .name(beanType.getName())
                .build();
    }

    public static BeanType toDomain(BeanTypeEntity beanTypeEntity){
        return BeanType.builder()
                .id(beanTypeEntity.getId())
                .name(beanTypeEntity.getName())
                .build();
    }
}
