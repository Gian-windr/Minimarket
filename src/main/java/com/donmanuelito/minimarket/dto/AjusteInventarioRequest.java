package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AjusteInventarioRequest(
        @NotNull Integer idProducto,
        /** Positivo suma stock, negativo resta (mermas, vencidos, etc.). */
        @NotNull Integer cantidad,
        @NotBlank String motivo) {
}
