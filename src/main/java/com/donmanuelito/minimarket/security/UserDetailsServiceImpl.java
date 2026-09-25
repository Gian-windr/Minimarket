package com.donmanuelito.minimarket.security;

import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.model.enums.EstadoUsuario;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String rol = usuario.getEmpleado().getRol().getNombreRol()
                .toUpperCase().replace(' ', '_');

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(rol)
                .disabled(usuario.getEstadoUsuario() == EstadoUsuario.INACTIVO)
                .accountLocked(usuario.getEstadoUsuario() == EstadoUsuario.BLOQUEADO)
                .build();
    }
}
