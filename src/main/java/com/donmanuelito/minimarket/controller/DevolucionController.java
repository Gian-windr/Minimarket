package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.DevolucionRequest;
import com.donmanuelito.minimarket.model.Devolucion;
import com.donmanuelito.minimarket.service.DevolucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final DevolucionService devolucionService;

    @GetMapping
    public List<Devolucion> listar() {
        return devolucionService.listar();
    }

    @GetMapping("/{id}")
    public Devolucion obtener(@PathVariable Integer id) {
        return devolucionService.obtener(id);
    }

    /** Registra la devolucion, restituye stock y genera nota de credito si aplica. */
    @PostMapping
    public ResponseEntity<Devolucion> registrar(@Valid @RequestBody DevolucionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucionService.registrar(request));
    }
}
