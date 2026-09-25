package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.UsuarioRequest;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.exception.ResourceNotFoundException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.model.enums.EstadoUsuario;
import com.donmanuelito.minimarket.repository.EmpleadoRepository;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtener(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public Usuario crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BusinessException("El username ya esta en uso: " + request.username());
        }
        Empleado empleado = empleadoRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", request.idEmpleado()));
        if (usuarioRepository.existsByEmpleadoIdEmpleado(empleado.getIdEmpleado())) {
            throw new BusinessException("El empleado ya tiene un usuario asignado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setEmpleado(empleado);
        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Integer id, EstadoUsuario estado) {
        Usuario usuario = obtener(id);
        usuario.setEstadoUsuario(estado);
        return usuarioRepository.save(usuario);
    }

    public Usuario resetearPassword(Integer id, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            throw new BusinessException("La password debe tener al menos 6 caracteres");
        }
        Usuario usuario = obtener(id);
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        return usuarioRepository.save(usuario);
    }
}
