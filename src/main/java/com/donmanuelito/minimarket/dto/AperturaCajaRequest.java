package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AperturaCajaRequest(
        @NotNull @DecimalMin("0.00") BigDecimal montoInicial) {
}
