package com.thecoffeetaster.coffee.infrastructure.adapter.in.dto.request;

import com.thecoffeetaster.coffee.domain.model.RoastLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CoffeeUpdate(
        String id,
        String beanType,
        String processingMethod,
        String originCountry,
        String originRegion,
        String originCity,
        @Min(value = 0, message = "Altitud can not be neagative")
        int altitude,
        @NotBlank(message = "The brand is mandatory")
        String brand,
        String tastingNotes,
        String roaster,
        @NotNull(message = "The roast level is mandatory")
        String roastLevel,
        @NotNull(message = "Price is mandatory")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price
) {}
