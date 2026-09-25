package com.donmanuelito.minimarket.repository;

import com.donmanuelito.minimarket.model.DetalleVenta;
import com.donmanuelito.minimarket.model.enums.EstadoVenta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);

    @Query("""
            select d.producto.idProducto, d.producto.nombreProducto,
                   sum(d.cantidadDetalle), coalesce(sum(d.subtotalDetalle), 0)
            from DetalleVenta d
            where d.venta.estadoVenta = :estado
              and d.venta.fechaVenta between :desde and :hasta
            group by d.producto.idProducto, d.producto.nombreProducto
            order by sum(d.cantidadDetalle) desc
            """)
    List<Object[]> findProductosMasVendidos(
            @Param("estado") EstadoVenta estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);
}
