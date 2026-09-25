package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    boolean existsByRuc(String ruc);
}
