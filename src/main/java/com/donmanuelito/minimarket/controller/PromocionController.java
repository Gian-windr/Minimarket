package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.PromocionRequest;
import com.donmanuelito.minimarket.model.Promocion;
import com.donmanuelito.minimarket.model.enums.EstadoPromocion;
import com.donmanuelito.minimarket.service.PromocionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService promocionService;

    @GetMapping
    public List<Promocion> listar() {
        return promocionService.listar();
    }

    @GetMapping("/{id}")
    public Promocion obtener(@PathVariable Integer id) {
        return promocionService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Promocion> crear(@Valid @RequestBody PromocionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promocionService.crear(request));
    }

    @PutMapping("/{id}")
    public Promocion actualizar(@PathVariable Integer id, @Valid @RequestBody PromocionRequest request) {
        return promocionService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Promocion cambiarEstado(@PathVariable Integer id, @RequestParam EstadoPromocion estado) {
        return promocionService.cambiarEstado(id, estado);
    }
}
