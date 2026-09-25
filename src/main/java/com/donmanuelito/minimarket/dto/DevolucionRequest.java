package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.enums.TipoDevolucion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DevolucionRequest(
        @NotNull Integer idVenta,
        String motivo,
        @NotNull TipoDevolucion tipoDevolucion,
        @NotEmpty @Valid List<DetalleDevolucionRequest> items) {
}
