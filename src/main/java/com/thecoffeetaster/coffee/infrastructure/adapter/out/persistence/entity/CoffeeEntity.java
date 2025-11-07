package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Entity
public class CoffeeEntity {
    @Id
    private UUID id;
    @ManyToOne
    private BeanTypeEntity beanType;
    @ManyToOne
    private ProcessingMethodEntity processingMethod;
    private String originCountry;
    private String originRegion;
    private String originCity;
    private Integer altitude;
    private String brand;
    private String tastingNotes;
    private String roaster;
    private String roastLevel;
    private BigDecimal price;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
