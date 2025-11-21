package com.thecoffeetaster.coffee.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CoffeeBrew {
    private UUID id;
    private Coffee coffee;
    private PreparationMethod preparationMethod;
    private TastingNote tastingNote;
    private LocalDateTime brewDate;
    private LocalDateTime roastDate;
    private String additionalNotes;
    private Integer coffeeWeight;
    private Integer waterWeight;
    private Integer waterTemperature;

}
