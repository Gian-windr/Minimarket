package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.VentaRequest;
import com.donmanuelito.minimarket.model.Venta;
import com.donmanuelito.minimarket.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listar();
    }

    /** Listado paginado: ?page=0&size=20&sort=fechaVenta,desc */
    @GetMapping("/paginado")
    public Page<Venta> listarPaginado(@PageableDefault(size = 20) Pageable pageable) {
        return ventaService.listarPaginado(pageable);
    }

    @GetMapping("/{id}")
    public Venta obtener(@PathVariable Integer id) {
        return ventaService.obtener(id);
    }

    @GetMapping("/rango")
    public List<Venta> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ventaService.listarPorFechas(desde, hasta);
    }

    @PostMapping
    public ResponseEntity<Venta> registrar(@Valid @RequestBody VentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(request));
    }

    /** Anula la venta y restituye el stock. */
    @PatchMapping("/{id}/anular")
    public Venta anular(@PathVariable Integer id) {
        return ventaService.anular(id);
    }
}
