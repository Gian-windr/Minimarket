package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.CategoriaProducto;
import com.donmanuelito.minimarket.repository.CategoriaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoRepository repository;

    @GetMapping
    public List<CategoriaProducto> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public CategoriaProducto obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", id));
    }

    @PostMapping
    public ResponseEntity<CategoriaProducto> crear(@RequestBody CategoriaProducto categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(categoria));
    }

    @PutMapping("/{id}")
    public CategoriaProducto actualizar(@PathVariable Integer id, @RequestBody CategoriaProducto cambios) {
        CategoriaProducto categoria = obtener(id);
        categoria.setNombreCategoria(cambios.getNombreCategoria());
        categoria.setDescripcion(cambios.getDescripcion());
        return repository.save(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repository.delete(obtener(id));
        return ResponseEntity.noContent().build();
    }
}
