package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas")
    public Map<String, Object> resumenVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.resumenVentas(desde, hasta);
    }

    @GetMapping("/productos-mas-vendidos")
    public List<Map<String, Object>> productosMasVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limite) {
        return reporteService.productosMasVendidos(desde, hasta, limite);
    }

    @GetMapping("/clientes-frecuentes")
    public List<Map<String, Object>> clientesFrecuentes(@RequestParam(defaultValue = "10") int limite) {
        return reporteService.clientesFrecuentes(limite);
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return reporteService.stockBajo();
    }
}
