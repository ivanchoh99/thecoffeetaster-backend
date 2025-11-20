package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Entity
@Table(name = "coffee")
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
