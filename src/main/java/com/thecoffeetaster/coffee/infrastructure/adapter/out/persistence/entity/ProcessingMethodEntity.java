package com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Getter
@Builder
@Entity
public class ProcessingMethodEntity {
    @Id
    private UUID id;
    private String name;
}
