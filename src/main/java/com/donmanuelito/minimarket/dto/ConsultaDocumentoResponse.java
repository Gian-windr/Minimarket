package com.donmanuelito.minimarket.dto;

import com.donmanuelito.minimarket.model.enums.TipoDocumento;

/**
 * Resultado normalizado de una consulta de DNI o RUC.
 * Sirve para autocompletar el formulario de cliente en el punto de venta.
 */
public record ConsultaDocumentoResponse(
        TipoDocumento tipoDocumento,
        String numero,
        String nombreCompleto,
        String direccion,
        /** Solo RUC: ACTIVO / BAJA DE OFICIO, etc. */
        String estado,
        /** Solo RUC: HABIDO / NO HABIDO. */
        String condicion) {

    public static ConsultaDocumentoResponse deDni(String numero, String nombreCompleto, String direccion) {
        return new ConsultaDocumentoResponse(TipoDocumento.DNI, numero, nombreCompleto, direccion, null, null);
    }

    public static ConsultaDocumentoResponse deRuc(String numero, String razonSocial, String direccion,
                                                  String estado, String condicion) {
        return new ConsultaDocumentoResponse(TipoDocumento.RUC, numero, razonSocial, direccion, estado, condicion);
    }
}
