package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {

    List<Devolucion> findByVentaIdVenta(Integer idVenta);
}
