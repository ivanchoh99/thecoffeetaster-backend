package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa;

import com.thecoffeetaster.coffee.domain.model.ProcessingMethod;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.ProcessingMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingMethodJpaRepository extends JpaRepository<ProcessingMethodEntity, UUID> {
}
