package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.enums.TipoComprobante;
import jakarta.validation.constraints.NotNull;

public record ComprobanteRequest(
        @NotNull Integer idVenta,
        @NotNull TipoComprobante tipoComprobante,
        /** DNI (boleta) o RUC (factura) del cliente. */
        String numDocumento,
        String nombreCliente,
        /** Solo para factura. */
        String razonSocial,
        /** Solo para factura. */
        String direccionFiscal) {
}
