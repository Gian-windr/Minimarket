package com.donmanuelito.minimarket.controller;

import com.donmanuelito.minimarket.dto.UsuarioRequest;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.model.enums.EstadoUsuario;
import com.donmanuelito.minimarket.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable Integer id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public Usuario cambiarEstado(@PathVariable Integer id, @RequestParam EstadoUsuario estado) {
        return usuarioService.cambiarEstado(id, estado);
    }

    @PatchMapping("/{id}/password")
    public Usuario resetearPassword(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return usuarioService.resetearPassword(id, body.get("password"));
    }
}
