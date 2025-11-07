package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.jpa;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.CoffeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoffeeJpaRepository extends JpaRepository<CoffeeEntity, UUID> {
}
