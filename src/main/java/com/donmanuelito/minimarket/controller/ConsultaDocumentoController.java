package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.ConsultaDocumentoResponse;
import com.donmanuelito.minimarket.service.FactilizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Consulta de DNI/RUC para autocompletar clientes y datos de facturacion. */
@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaDocumentoController {

    private final FactilizaService factilizaService;

    @GetMapping("/dni/{dni}")
    public ConsultaDocumentoResponse consultarDni(@PathVariable String dni) {
        return factilizaService.consultarDni(dni);
    }

    @GetMapping("/ruc/{ruc}")
    public ConsultaDocumentoResponse consultarRuc(@PathVariable String ruc) {
        return factilizaService.consultarRuc(ruc);
    }
}
