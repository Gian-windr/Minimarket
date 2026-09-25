package com.donmanuelito.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EmpleadoRequest(
        @NotBlank String nombreEmpleado,
        @NotBlank String apellidoEmpleado,
        LocalDateTime fechaContratacion,
        @NotNull Integer idRol) {
}
