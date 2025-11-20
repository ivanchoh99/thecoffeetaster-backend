package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "processing_method")
public class ProcessingMethodEntity {
    @Id
    private UUID id;
    private String name;
}
