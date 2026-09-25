package com.donmanuelito.minimarket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CompraRequest(
        @NotNull Integer idProveedor,
        String numeroFacturaProveedor,
        @NotEmpty @Valid List<DetalleCompraRequest> items) {
}
