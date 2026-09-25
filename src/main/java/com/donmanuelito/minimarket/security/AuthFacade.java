package com.donmanuelito.minimarket.security;

import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Acceso al usuario/empleado autenticado en la peticion actual. */
@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UsuarioRepository usuarioRepository;

    public Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("No hay usuario autenticado");
        }
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BusinessException("Usuario autenticado no existe en el sistema"));
    }

    public Empleado empleadoActual() {
        return usuarioActual().getEmpleado();
    }
}
