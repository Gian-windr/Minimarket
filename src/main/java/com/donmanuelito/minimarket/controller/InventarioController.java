package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.AjusteInventarioRequest;
import com.donmanuelito.minimarket.model.MovimientoInventario;
import com.donmanuelito.minimarket.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/movimientos")
    public List<MovimientoInventario> listar() {
        return inventarioService.listar();
    }

    /** Historial de movimientos (kardex) de un producto. */
    @GetMapping("/kardex/{idProducto}")
    public List<MovimientoInventario> kardex(@PathVariable Integer idProducto) {
        return inventarioService.kardex(idProducto);
    }

    /** Ajuste manual de stock: cantidad positiva suma, negativa resta. */
    @PostMapping("/ajuste")
    public ResponseEntity<MovimientoInventario> ajustar(@Valid @RequestBody AjusteInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.ajustar(request));
    }
}
