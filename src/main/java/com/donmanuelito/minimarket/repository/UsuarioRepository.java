package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmpleadoIdEmpleado(Integer idEmpleado);
}
