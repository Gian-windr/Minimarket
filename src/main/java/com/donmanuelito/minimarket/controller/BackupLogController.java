package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.BackupLog;
import com.donmanuelito.minimarket.repository.BackupLogRepository;
import com.donmanuelito.minimarket.security.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupLogController {

    private final BackupLogRepository repository;
    private final AuthFacade authFacade;

    @GetMapping
    public List<BackupLog> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public BackupLog obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BackupLog", id));
    }

    /** Registra un backup realizado; el responsable es el usuario autenticado. */
    @PostMapping
    public ResponseEntity<BackupLog> registrar(@RequestBody BackupLog backup) {
        backup.setUsuarioResponsable(authFacade.usuarioActual());
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(backup));
    }
}
