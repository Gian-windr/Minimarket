package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.ComprobanteRequest;
import com.donmanuelito.minimarket.model.Comprobante;
import com.donmanuelito.minimarket.service.ComprobanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @GetMapping
    public List<Comprobante> listar() {
        return comprobanteService.listar();
    }

    @GetMapping("/{id}")
    public Comprobante obtener(@PathVariable Integer id) {
        return comprobanteService.obtener(id);
    }

    @GetMapping("/venta/{idVenta}")
    public Comprobante obtenerPorVenta(@PathVariable Integer idVenta) {
        return comprobanteService.obtenerPorVenta(idVenta);
    }

    /** Emite boleta o factura para una venta. */
    @PostMapping
    public ResponseEntity<Comprobante> emitir(@Valid @RequestBody ComprobanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprobanteService.emitir(request));
    }

    /** Representacion impresa del comprobante en formato ticket de 80 mm. */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer id) {
        Comprobante comprobante = comprobanteService.obtener(id);
        byte[] pdf = comprobanteService.obtenerPdf(id);
        String nombre = comprobante.getSerieComprobante() + "-" + comprobante.getNumeroComprobante() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                .body(pdf);
    }
}
