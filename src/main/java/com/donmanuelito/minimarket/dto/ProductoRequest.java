package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank String nombreProducto,
        String descripcionProducto,
        @NotNull @DecimalMin("0.01") BigDecimal precioProducto,
        BigDecimal precioCompra,
        @NotNull @Min(0) Integer stockProducto,
        @NotNull @Min(0) Integer stockMinimo,
        Boolean tipoCorte,
        String imagen,
        String codigoBarras,
        Integer idTipoProducto,
        Integer idCategoriaProducto,
        Integer idUnidadMedida) {
}
