package com.donmanuelito.minimarket.dto.factiliza;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Respuesta cruda de GET /v1/ruc/info/{ruc} en Factiliza. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FactilizaRucResponse(
        boolean success,
        String message,
        FactilizaRucData data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FactilizaRucData(
            String numero,
            @JsonProperty("nombre_o_razon_social") String nombreORazonSocial,
            String estado,
            String condicion,
            @JsonProperty("direccion_completa") String direccionCompleta,
            String direccion) {
    }
}
