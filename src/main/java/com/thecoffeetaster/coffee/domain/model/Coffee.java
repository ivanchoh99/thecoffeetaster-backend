package com.thecoffeetaster.coffee.domain.model;

import com.thecoffeetaster.coffee.domain.exception.CoffeeException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class Coffee {
    private UUID id;
    private BeanType beanType;
    private ProcessingMethod processingMethod;
    private String originCountry;
    private String originRegion;
    private String originCity;
    private Integer altitude;
    private String brand;
    private String roaster;
    private RoastLevel roastLevel;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Coffee(Builder builder){
        this.id = builder.id;
        this.beanType = builder.beanType;
        this.processingMethod = builder.processingMethod;
        this.originCountry = builder.originCountry;
        this.originRegion = builder.originRegion;
        this.originCity = builder.originCity;
        this.altitude = builder.altitude;
        this.brand = builder.brand;
        this.roaster = builder.roaster;
        this.roastLevel = builder.roastLevel;
        this.price = builder.price;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static class Builder {
        private final String brand;
        private final BigDecimal price;

        private UUID id;
        private BeanType beanType;
        private ProcessingMethod processingMethod;
        private Integer altitude;
        private String originCountry;
        private String originRegion;
        private String originCity;
        private String roaster;
        private RoastLevel roastLevel = RoastLevel.UNKNOWN;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder(String brand, BigDecimal price) {
            this.brand = brand;
            this.price = price;
        }

        public Builder id(UUID id){
            this.id = id;
            return this;
        }

        public Builder altitude(int altitude){
            this.altitude = altitude;
            return this;
        }

        public Builder beanType(BeanType beanType) {
            this.beanType = beanType;
            return this;
        }

        public Builder processingMethod(ProcessingMethod processingMethod) {
            this.processingMethod = processingMethod;
            return this;
        }

        public Builder originCountry(String originCountry) {
            this.originCountry = originCountry;
            return this;
        }

        public Builder originRegion(String originRegion) {
            this.originRegion = originRegion;
            return this;
        }

        public Builder originCity(String originCity) {
            this.originCity = originCity;
            return this;
        }


        public Builder roaster(String roaster) {
            this.roaster = roaster;
            return this;
        }

        public Builder roastLevel(RoastLevel roastLevel) {
            this.roastLevel = roastLevel;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Coffee build() {

            if (altitude < 0) {
                throw new CoffeeException("Altitude cannot be negative");
            }
            if (brand == null || brand.isBlank()) {
                throw new CoffeeException("Brand cannot be null or empty");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new CoffeeException("Price cannot be null or negative");
            }


            if (this.createdAt == null) {
                this.createdAt = LocalDateTime.now();
            }
            if (this.updatedAt == null) {
                this.updatedAt = LocalDateTime.now();
            }

            return new Coffee(this);
        }
    }

    public static Builder builder(String brand, BigDecimal price) {
        return new Builder(brand, price);
    }
}