package com.thecoffeetaster.coffee.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ProcessingMethod {
    private UUID id;
    private String name;
}
