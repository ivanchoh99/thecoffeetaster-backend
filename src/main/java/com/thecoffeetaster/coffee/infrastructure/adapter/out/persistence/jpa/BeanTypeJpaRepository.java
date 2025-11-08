package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa;

import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.BeanTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeanTypeJpaRepository extends JpaRepository<BeanTypeEntity, UUID> {
}
