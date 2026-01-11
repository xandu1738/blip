package com.ceres.blip.models.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionColors {
    BLUE("blue"), GREEN("green"), ORANGE("orange");
    private final String color;
}
