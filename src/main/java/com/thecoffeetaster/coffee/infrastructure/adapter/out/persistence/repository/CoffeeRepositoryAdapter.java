package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.repository;

import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.CoffeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeeRepositoryAdapter extends JpaRepository<CoffeeEntity, UUID> {
}
