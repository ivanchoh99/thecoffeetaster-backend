package com.thecoffeetaster.coffee.domain.model;

public enum RoastLevel {
    LIGHT, MEDIUM_LIGHT, MEDIUM, MEDIUM_DARK, DARK, UNKNOWN;

    public static RoastLevel fromString(String value){
        for(RoastLevel roastLevel: RoastLevel.values()){
            if(roastLevel.name().equalsIgnoreCase(value)) return roastLevel;
        }
        return RoastLevel.UNKNOWN;
    }
}
