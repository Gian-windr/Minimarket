package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Integer> {

    List<Compra> findByProveedorIdProveedor(Integer idProveedor);

    List<Compra> findByFechaCompraBetween(LocalDateTime desde, LocalDateTime hasta);
}
