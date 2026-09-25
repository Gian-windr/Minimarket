package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNumDocumento(String numDocumento);

    boolean existsByNumDocumento(String numDocumento);
}
