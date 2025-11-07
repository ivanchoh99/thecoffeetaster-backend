package com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request;

import com.thecoffeetaster.coffee.domain.model.RoastLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record CoffeePatch(
        String beanType,
        String processingMethod,
        String originCountry,
        String originRegion,
        String originCity,
        @Min(value = 0, message = "Altitud can not be neagative")
        Integer altitude,
        String brand,
        String tastingNotes,
        String roaster,
        RoastLevel roastLevel,
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price
) {}
