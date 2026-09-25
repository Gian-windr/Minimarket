package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetalleDevolucionRequest(
        @NotNull Integer idDetalleVenta,
        @NotNull @Min(1) Integer cantidad) {
}
