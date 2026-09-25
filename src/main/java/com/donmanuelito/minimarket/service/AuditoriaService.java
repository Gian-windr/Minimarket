package com.donmanuelito.minimarket.service;

import com.donmanuelito.minimarket.model.Auditoria;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.repository.AuditoriaRepository;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    /** Registra una accion usando el usuario autenticado actual (si existe). */
    public void registrar(String accion, String tabla, String descripcion) {
        Usuario usuario = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        }
        registrar(usuario, accion, tabla, descripcion, null);
    }

    public void registrar(Usuario usuario, String accion, String tabla, String descripcion, String ip) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccionAuditoria(accion);
        auditoria.setTablaAfectada(tabla);
        auditoria.setDescripcionAuditoria(descripcion);
        auditoria.setIpClienteAuditoria(ip);
        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listar() {
        return auditoriaRepository.findAllByOrderByFechaHoraAuditoriaDesc();
    }
}
