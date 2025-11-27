// PreparationMethod.java
package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import com.thecoffeetaster.coffee.domain.model.TypeGrind;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "preparation_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreparationMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer recommendedTime;
    @Enumerated(EnumType.STRING)
    private TypeGrind recommendedGrind;
    private Double waterCoffeeRatio;
}