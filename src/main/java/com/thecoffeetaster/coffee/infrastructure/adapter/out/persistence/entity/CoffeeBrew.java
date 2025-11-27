// CoffeeBrew.java
package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import com.thecoffeetaster.coffee.domain.model.Coffee;
import com.thecoffeetaster.coffee.domain.model.PreparationMethod;
import com.thecoffeetaster.coffee.domain.model.TastingNote;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coffee_brews")
public class CoffeeBrew {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_id", nullable = false)
    private Coffee coffee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparation_method_id", nullable = false)
    private PreparationMethod preparationMethod;
    @Embedded
    private TastingNote tastingNote;
    @Column(name = "brew_date", nullable = false)
    private LocalDateTime brewDate;
    private LocalDateTime roastDate;
    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;
    private Integer coffeeWeight;
    private Integer waterWeight;
    private Integer waterTemperature;
}