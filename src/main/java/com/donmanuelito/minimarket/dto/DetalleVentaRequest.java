package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetalleVentaRequest(
        @NotNull Integer idProducto,
        @NotNull @Min(1) Integer cantidad) {
}
