package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.EmpleadoRequest;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.enums.Estado;
import com.donmanuelito.minimarket.repository.EmpleadoRepository;
import com.donmanuelito.minimarket.repository.RolRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoRepository repository;
    private final RolRepository rolRepository;

    @GetMapping
    public List<Empleado> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Empleado obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", id));
    }

    @PostMapping
    public ResponseEntity<Empleado> crear(@Valid @RequestBody EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        aplicar(request, empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(empleado));
    }

    @PutMapping("/{id}")
    public Empleado actualizar(@PathVariable Integer id, @Valid @RequestBody EmpleadoRequest request) {
        Empleado empleado = obtener(id);
        aplicar(request, empleado);
        return repository.save(empleado);
    }

    /** Baja logica (estado INACTIVO). */
    @DeleteMapping("/{id}")
    public Empleado eliminar(@PathVariable Integer id) {
        Empleado empleado = obtener(id);
        empleado.setEstadoEmpleado(Estado.INACTIVO);
        return repository.save(empleado);
    }

    private void aplicar(EmpleadoRequest request, Empleado empleado) {
        empleado.setNombreEmpleado(request.nombreEmpleado());
        empleado.setApellidoEmpleado(request.apellidoEmpleado());
        empleado.setFechaContratacion(request.fechaContratacion());
        empleado.setRol(rolRepository.findById(request.idRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", request.idRol())));
    }
}
