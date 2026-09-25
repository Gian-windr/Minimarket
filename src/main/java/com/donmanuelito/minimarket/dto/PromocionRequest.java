package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.enums.TipoPromocion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromocionRequest(
        @NotBlank String nombrePromocion,
        String descripcionPromocion,
        @NotNull TipoPromocion tipoPromocion,
        @NotNull @DecimalMin("0.01") BigDecimal descuentoPromocion,
        @NotNull LocalDate fechaInicioPromo,
        @NotNull LocalDate fechaFinPromo,
        List<Integer> idProductos) {
}
