package com.donmanuelito.minimarket.dto.factiliza;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Respuesta cruda de GET /v1/dni/info/{dni} en Factiliza. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FactilizaDniResponse(
        boolean success,
        String message,
        FactilizaDniData data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FactilizaDniData(
            String numero,
            @JsonProperty("nombre_completo") String nombreCompleto,
            String nombres,
            @JsonProperty("apellido_paterno") String apellidoPaterno,
            @JsonProperty("apellido_materno") String apellidoMaterno,
            String direccion) {
    }
}
