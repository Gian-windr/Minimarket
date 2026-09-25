package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.CompraRequest;
import com.donmanuelito.minimarket.model.Compra;
import com.donmanuelito.minimarket.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public List<Compra> listar() {
        return compraService.listar();
    }

    @GetMapping("/{id}")
    public Compra obtener(@PathVariable Integer id) {
        return compraService.obtener(id);
    }

    /** Registra la compra a proveedor y actualiza el stock. */
    @PostMapping
    public ResponseEntity<Compra> registrar(@Valid @RequestBody CompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.registrar(request));
    }
}
