package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.Producto;
import com.donmanuelito.minimarket.model.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);

    List<Producto> findByEstadoProducto(Estado estado);

    long countByEstadoProducto(Estado estado);

    @Query("select p from Producto p where p.stockProducto <= p.stockMinimo and p.estadoProducto = :estado")
    List<Producto> findStockBajo(@Param("estado") Estado estado);
}
