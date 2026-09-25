package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.TipoProducto;
import com.donmanuelito.minimarket.repository.TipoProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-producto")
@RequiredArgsConstructor
public class TipoProductoController {

    private final TipoProductoRepository repository;

    @GetMapping
    public List<TipoProducto> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public TipoProducto obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoProducto", id));
    }

    @PostMapping
    public ResponseEntity<TipoProducto> crear(@RequestBody TipoProducto tipoProducto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(tipoProducto));
    }

    @PutMapping("/{id}")
    public TipoProducto actualizar(@PathVariable Integer id, @RequestBody TipoProducto cambios) {
        TipoProducto tipo = obtener(id);
        tipo.setNombreTipoProducto(cambios.getNombreTipoProducto());
        tipo.setDescripcion(cambios.getDescripcion());
        return repository.save(tipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repository.delete(obtener(id));
        return ResponseEntity.noContent().build();
    }
}
