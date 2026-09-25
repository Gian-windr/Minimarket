package com.donmanuelito.minimarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Devuelve 401 y 403 como JSON con el mismo formato que GlobalExceptionHandler,
 * para que el frontend distinga "falta token" de "sin permisos".
 */
@Component
@RequiredArgsConstructor
public class RespuestaErrorSeguridad {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint noAutenticado() {
        return (request, response, excepcion) ->
                escribir(response, HttpStatus.UNAUTHORIZED, "No autenticado: falta el token o ya expiro");
    }

    public AccessDeniedHandler sinPermisos() {
        return (request, response, excepcion) ->
                escribir(response, HttpStatus.FORBIDDEN, "No tiene permisos para esta operacion");
    }

    private void escribir(HttpServletResponse response, HttpStatus estado, String mensaje) throws IOException {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), cuerpo);
    }
}
