package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Rol;
import com.donmanuelito.minimarket.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolRepository repository;

    @GetMapping
    public List<Rol> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Rol obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(rol));
    }

    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Integer id, @RequestBody Rol cambios) {
        Rol rol = obtener(id);
        rol.setNombreRol(cambios.getNombreRol());
        return repository.save(rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repository.delete(obtener(id));
        return ResponseEntity.noContent().build();
    }
}
