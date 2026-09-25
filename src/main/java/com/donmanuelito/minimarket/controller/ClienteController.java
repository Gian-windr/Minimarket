package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Cliente;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository repository;

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    @GetMapping("/documento/{numDocumento}")
    public Cliente obtenerPorDocumento(@PathVariable String numDocumento) {
        return repository.findByNumDocumento(numDocumento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con documento: " + numDocumento));
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        if (repository.existsByNumDocumento(cliente.getNumDocumento())) {
            throw new BusinessException("Ya existe un cliente con documento: " + cliente.getNumDocumento());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(cliente));
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id, @RequestBody Cliente cambios) {
        Cliente cliente = obtener(id);
        cliente.setNombreCliente(cambios.getNombreCliente());
        cliente.setApellidoCliente(cambios.getApellidoCliente());
        cliente.setTipoDocumento(cambios.getTipoDocumento());
        cliente.setNumDocumento(cambios.getNumDocumento());
        cliente.setTelefono(cambios.getTelefono());
        cliente.setDireccion(cambios.getDireccion());
        cliente.setEmail(cambios.getEmail());
        return repository.save(cliente);
    }

    /** Baja logica (estado INACTIVO). */
    @DeleteMapping("/{id}")
    public Cliente eliminar(@PathVariable Integer id) {
        Cliente cliente = obtener(id);
        cliente.setEstadoCliente(Estado.INACTIVO);
        return repository.save(cliente);
    }
}
