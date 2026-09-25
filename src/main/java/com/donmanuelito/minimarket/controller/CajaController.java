package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.AperturaCajaRequest;
import com.donmanuelito.minimarket.dto.CierreCajaRequest;
import com.donmanuelito.minimarket.model.Caja;
import com.donmanuelito.minimarket.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @GetMapping
    public List<Caja> listar() {
        return cajaService.listar();
    }

    @GetMapping("/{id}")
    public Caja obtener(@PathVariable Integer id) {
        return cajaService.obtener(id);
    }

    /** Caja abierta del empleado autenticado. */
    @GetMapping("/actual")
    public Caja actual() {
        return cajaService.actual();
    }

    @PostMapping("/abrir")
    public ResponseEntity<Caja> abrir(@Valid @RequestBody AperturaCajaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaService.abrir(request));
    }

    /** Cierra la caja del empleado autenticado con arqueo. */
    @PostMapping("/cerrar")
    public Caja cerrar(@Valid @RequestBody CierreCajaRequest request) {
        return cajaService.cerrar(request);
    }
}
