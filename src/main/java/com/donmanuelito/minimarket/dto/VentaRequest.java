package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.enums.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VentaRequest(
        @NotNull MetodoPago metodoPago,
        Integer idCliente,
        @NotEmpty @Valid List<DetalleVentaRequest> items) {
}
