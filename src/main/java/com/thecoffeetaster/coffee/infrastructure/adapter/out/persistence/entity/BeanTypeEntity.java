package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "bean_type")
public class BeanTypeEntity {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
}
