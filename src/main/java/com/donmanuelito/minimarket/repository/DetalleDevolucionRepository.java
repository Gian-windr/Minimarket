package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Integer> {

    @Query("""
            select coalesce(sum(d.cantidadDevolucion), 0) from DetalleDevolucion d
            where d.detalleVenta.idDetalleVenta = :idDetalleVenta
            """)
    int totalDevueltoPorDetalleVenta(@Param("idDetalleVenta") Integer idDetalleVenta);
}
