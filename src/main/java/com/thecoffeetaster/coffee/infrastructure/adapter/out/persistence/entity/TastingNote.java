// TastingNote.java
package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

@Embeddable
public class TastingNote {
    private String aroma;
    private Integer acidity;
    private Integer body;
    private Integer sweetness;
    private Integer bitterness;
    private String generalFlavor;
    private Integer score;
}