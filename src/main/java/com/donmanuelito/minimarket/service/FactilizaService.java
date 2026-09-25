package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.ConsultaDocumentoResponse;
import com.donmanuelito.minimarket.dto.factiliza.FactilizaDniResponse;
import com.donmanuelito.minimarket.dto.factiliza.FactilizaRucResponse;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Consulta datos de personas (DNI) y empresas (RUC) contra la API de Factiliza,
 * para autocompletar clientes y emitir facturas con razon social valida.
 */
@Service
public class FactilizaService {

    private final RestClient restClient;
    private final boolean habilitado;

    public FactilizaService(RestClient.Builder builder,
                            @Value("${app.factiliza.base-url}") String baseUrl,
                            @Value("${app.factiliza.api-key}") String apiKey) {
        this.habilitado = apiKey != null && !apiKey.isBlank();
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + (apiKey == null ? "" : apiKey))
                .build();
    }

    public ConsultaDocumentoResponse consultarDni(String dni) {
        verificarHabilitado();
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new BusinessException("El DNI debe tener 8 digitos");
        }

        FactilizaDniResponse respuesta = ejecutar(
                () -> restClient.get().uri("/dni/info/{dni}", dni).retrieve().body(FactilizaDniResponse.class));

        if (respuesta == null || !respuesta.success() || respuesta.data() == null) {
            throw new ResourceNotFoundException("No se encontro informacion para el DNI " + dni);
        }

        var data = respuesta.data();
        String nombre = data.nombreCompleto() != null
                ? data.nombreCompleto()
                : String.join(" ", nn(data.nombres()), nn(data.apellidoPaterno()), nn(data.apellidoMaterno())).trim();
        return ConsultaDocumentoResponse.deDni(data.numero() != null ? data.numero() : dni, nombre, data.direccion());
    }

    public ConsultaDocumentoResponse consultarRuc(String ruc) {
        verificarHabilitado();
        if (ruc == null || !ruc.matches("\\d{11}")) {
            throw new BusinessException("El RUC debe tener 11 digitos");
        }

        FactilizaRucResponse respuesta = ejecutar(
                () -> restClient.get().uri("/ruc/info/{ruc}", ruc).retrieve().body(FactilizaRucResponse.class));

        if (respuesta == null || !respuesta.success() || respuesta.data() == null) {
            throw new ResourceNotFoundException("No se encontro informacion para el RUC " + ruc);
        }

        var data = respuesta.data();
        String direccion = data.direccionCompleta() != null ? data.direccionCompleta() : data.direccion();
        return ConsultaDocumentoResponse.deRuc(
                data.numero() != null ? data.numero() : ruc,
                data.nombreORazonSocial(), direccion, data.estado(), data.condicion());
    }

    private void verificarHabilitado() {
        if (!habilitado) {
            throw new BusinessException(
                    "La consulta de documentos no esta configurada: falta API_KEY de Factiliza en el .env");
        }
    }

    /** Traduce cualquier fallo de red o de la API externa a un error de negocio legible. */
    private <T> T ejecutar(java.util.function.Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (RestClientException e) {
            throw new BusinessException("No se pudo consultar el documento en Factiliza: " + e.getMessage());
        }
    }

    private String nn(String valor) {
        return valor == null ? "" : valor;
    }
}
