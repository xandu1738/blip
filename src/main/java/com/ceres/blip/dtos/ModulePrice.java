package com.ceres.blip.dtos;

import java.math.BigDecimal;

public record ModulePrice(
        BigDecimal monthly,
        BigDecimal annual) {
}
