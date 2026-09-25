package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProductoIdProductoOrderByFechaMovimientoDesc(Integer idProducto);
}
