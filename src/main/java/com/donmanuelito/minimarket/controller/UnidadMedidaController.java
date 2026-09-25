package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.UnidadMedida;
import com.donmanuelito.minimarket.repository.UnidadMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private final UnidadMedidaRepository repository;

    @GetMapping
    public List<UnidadMedida> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public UnidadMedida obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UnidadMedida", id));
    }

    @PostMapping
    public ResponseEntity<UnidadMedida> crear(@RequestBody UnidadMedida unidad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(unidad));
    }

    @PutMapping("/{id}")
    public UnidadMedida actualizar(@PathVariable Integer id, @RequestBody UnidadMedida cambios) {
        UnidadMedida unidad = obtener(id);
        unidad.setNombreUnidad(cambios.getNombreUnidad());
        return repository.save(unidad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repository.delete(obtener(id));
        return ResponseEntity.noContent().build();
    }
}
