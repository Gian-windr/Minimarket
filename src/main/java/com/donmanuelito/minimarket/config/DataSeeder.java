package com.donmanuelito.minimarket.config;

import com.donmanuelito.minimarket.model.Empleado;
import com.donmanuelito.minimarket.model.Rol;
import com.donmanuelito.minimarket.model.UnidadMedida;
import com.donmanuelito.minimarket.model.Usuario;
import com.donmanuelito.minimarket.repository.EmpleadoRepository;
import com.donmanuelito.minimarket.repository.RolRepository;
import com.donmanuelito.minimarket.repository.UnidadMedidaRepository;
import com.donmanuelito.minimarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/** Datos iniciales: roles, unidades de medida y usuario administrador. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(new Rol("ADMINISTRADOR"), new Rol("CAJERO")));
        }

        if (unidadMedidaRepository.count() == 0) {
            unidadMedidaRepository.saveAll(List.of(
                    new UnidadMedida("Unidad"),
                    new UnidadMedida("Kilogramo"),
                    new UnidadMedida("Litro"),
                    new UnidadMedida("Caja"),
                    new UnidadMedida("Paquete")));
        }

        if (usuarioRepository.count() == 0) {
            Rol admin = rolRepository.findByNombreRol("ADMINISTRADOR").orElseThrow();

            Empleado empleado = new Empleado();
            empleado.setNombreEmpleado("Admin");
            empleado.setApellidoEmpleado("Sistema");
            empleado.setFechaContratacion(LocalDateTime.now());
            empleado.setRol(admin);
            empleado = empleadoRepository.save(empleado);

            Usuario usuario = new Usuario();
            usuario.setUsername("admin");
            usuario.setPasswordHash(passwordEncoder.encode("admin123"));
            usuario.setEmpleado(empleado);
            usuarioRepository.save(usuario);
        }
    }
}
