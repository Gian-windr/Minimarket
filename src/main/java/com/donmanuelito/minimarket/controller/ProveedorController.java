package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Proveedor;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorRepository repository;

    @GetMapping
    public List<Proveedor> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Proveedor obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        if (repository.existsByRuc(proveedor.getRuc())) {
            throw new BusinessException("Ya existe un proveedor con RUC: " + proveedor.getRuc());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(proveedor));
    }

    @PutMapping("/{id}")
    public Proveedor actualizar(@PathVariable Integer id, @RequestBody Proveedor cambios) {
        Proveedor proveedor = obtener(id);
        proveedor.setRazonSocial(cambios.getRazonSocial());
        proveedor.setRuc(cambios.getRuc());
        proveedor.setTelefono(cambios.getTelefono());
        proveedor.setDireccion(cambios.getDireccion());
        proveedor.setEmail(cambios.getEmail());
        return repository.save(proveedor);
    }

    /** Baja logica (estado INACTIVO). */
    @DeleteMapping("/{id}")
    public Proveedor eliminar(@PathVariable Integer id) {
        Proveedor proveedor = obtener(id);
        proveedor.setEstadoProveedor(Estado.INACTIVO);
        return repository.save(proveedor);
    }
}
