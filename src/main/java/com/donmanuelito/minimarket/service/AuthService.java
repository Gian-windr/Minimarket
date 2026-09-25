package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.dto.LoginRequest;
import com.donmanuelito.minimarket.dto.LoginResponse;
import com.donmanuelito.minimarket.exception.BusinessException;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import com.donmanuelito.minimarket.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public LoginResponse login(LoginRequest request, String ip) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (DisabledException e) {
            throw new BusinessException("El usuario esta inactivo");
        } catch (LockedException e) {
            throw new BusinessException("El usuario esta bloqueado");
        }

        Usuario usuario = usuarioRepository.findByUsername(request.username()).orElseThrow();
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String rol = usuario.getEmpleado().getRol().getNombreRol();
        String token = jwtService.generateToken(usuario.getUsername(), rol);
        String nombreEmpleado = usuario.getEmpleado().getNombreEmpleado()
                + " " + usuario.getEmpleado().getApellidoEmpleado();

        auditoriaService.registrar(usuario, "LOGIN", "usuario",
                "Inicio de sesion de " + usuario.getUsername(), ip);

        return LoginResponse.bearer(token, usuario.getUsername(), rol, nombreEmpleado);
    }
}
